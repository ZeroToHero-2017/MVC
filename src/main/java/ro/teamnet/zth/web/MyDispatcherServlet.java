package ro.teamnet.zth.web;

import ro.teamnet.zth.api.annotations.MyController;
import ro.teamnet.zth.api.annotations.MyRequestMethod;
import ro.teamnet.zth.fmk.MethodAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static ro.teamnet.zth.fmk.AnnotationScanUtils.getClasses;

/**
 * Created by Gabriel.Tabus on 7/20/2017.
 */
public class MyDispatcherServlet extends HttpServlet {
    Map<String, MethodAttributes> controllersMethods;

    public void init(){
        // load controllers
        Iterable<Class> classes = null;
        try {
            classes = getClasses(new String("ro.teamnet.zth.appl.controller"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        controllersMethods = new HashMap<String,MethodAttributes>();

        for(Class each : classes){
            if(each.isAnnotationPresent(MyController.class)){
                String path = ((MyController)each.getAnnotation(MyController.class)).urlPath();
                for(Method method : each.getDeclaredMethods()){
                    method.setAccessible(true);

                    String methodPath = new String(path);
                    methodPath += ((MyRequestMethod)method.getAnnotation(MyRequestMethod.class)).urlPath();
                    MethodAttributes elem = new MethodAttributes();

                    elem.setControllerClass(each.getCanonicalName());
                    elem.setMethodName(method.getName());
                    elem.setMethodType(((MyRequestMethod)method.getAnnotation(MyRequestMethod.class)).methodType());

                    controllersMethods.put(methodPath, elem);
                    System.out.println(methodPath);
                }
            }
        }
    }

    public static void main(){
        System.out.println(String.class.getSimpleName());
    }


    private void dispatchReply(HttpServletRequest request, HttpServletResponse response, String requestType) throws IOException {
        try {
            Object resultToDisplay = dispatch(request, requestType);
            reply(response, resultToDisplay);
        } catch (Exception e) {
            sendExceptionError(e, response);
        }
    }

    private void sendExceptionError(Exception e, HttpServletResponse response) throws IOException {
        response.getWriter().println(e.getMessage());
    }

    private Object dispatch(HttpServletRequest request, String requestType){

        String key = request.getPathInfo();

        MethodAttributes method = controllersMethods.get(key);

        Object toReturn = null;

        try {
            Class myClass = Class.forName(method.getControllerClass());
            Object executionInstance = myClass.newInstance();
            executionInstance.getClass().getDeclaredMethod(method.getMethodName()).setAccessible(true);
            toReturn = executionInstance.getClass().getDeclaredMethod(method.getMethodName()).invoke(executionInstance);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    private void reply(HttpServletResponse response, Object resultToDisplay){
        try {
            response.setContentType("text/html");
            response.getWriter().print(resultToDisplay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatchReply(request, response, "POST");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatchReply(request, response, "GET");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatchReply(request, response, "PUT");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatchReply(request, response, "DELETE");
    }
}

package ro.teamnet.zth.appl.controller;

import ro.teamnet.zth.api.annotations.Z2HController;
import ro.teamnet.zth.api.annotations.Z2HRequestMethod;
import ro.teamnet.zth.api.annotations.Z2HRequestObject;
import ro.teamnet.zth.api.annotations.Z2HRequestParam;
import ro.teamnet.zth.appl.dao.JobDao;
import ro.teamnet.zth.appl.domain.Job;
import ro.teamnet.zth.appl.service.JobService;
import ro.teamnet.zth.fmk.domain.HttpMethod;

import java.util.List;

@Z2HController(urlPath = "/jobs")
public class JobController {
    private JobService jobService;

    public JobController() {
        jobService = new JobService() {
            JobDao dao = new JobDao();
            @Override
            public List<Job> findAll() {
                return dao.getAllJobs();
            }

            @Override
            public Job findOne(String jobId) {
                return dao.getJobById(jobId);
            }

            @Override
            public Boolean delete(String jobId) {
                Job job = new Job();
                job.setId(jobId);
                dao.deleteJob(job);
                return true;
            }

            @Override
            public Job save(Job job) {
                return dao.insertJob(job);
            }

            @Override
            public Job update(Job job) {
                return dao.updateJob(job);
            }
        };
    }

    @Z2HRequestMethod(urlPath = "/all")
    public List<Job> getAll() {
        return jobService.findAll();
    }

    @Z2HRequestMethod(urlPath = "/one")
    public Job getOne(@Z2HRequestParam(name = "jobId") String jobId) {
        return jobService.findOne(jobId);
    }

    @Z2HRequestMethod(urlPath = "/one", methodType = HttpMethod.DELETE)
    public Boolean deleteOneEmployee(@Z2HRequestParam(name = "jobId") String jobId) {
        return jobService.delete(jobId);
    }

    @Z2HRequestMethod(urlPath = "/create", methodType = HttpMethod.POST)
    public Job saveEmployee(@Z2HRequestObject Job job) {
        return jobService.save(job);
    }

    @Z2HRequestMethod(urlPath = "/edit", methodType = HttpMethod.PUT)
    public Job updateEmployee(@Z2HRequestObject Job job) {
        return jobService.update(job);
    }

}

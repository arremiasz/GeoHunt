package com.geohunt.backend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class SubmissionsService {

    @Autowired
    AccountService accountService;

    @Autowired
    ChallengesRepository challengesRepository;

    @Autowired
    SubmissionsRepository submissionsRepository;

    // Save Submission

    public Submissions saveSubmission(Submissions submission, long uid, long cid) throws IllegalArgumentException {
        // TODO: Implement challenges when merging with Location Generation
        Account account = accountService.getAccountById(uid);
        Challenges challenge = null;

        // Set Challenge and Account that the submission is tied to.
        submission.setSubmitter(account);
        submission.setChallenge(challenge);

        if(!submission.validate()) {
            throw new IllegalArgumentException("Submission Missing Data");
        }

        // Set Submission Time
        submission.setSubmissionTimeToNow();

        // Add Submission to Database
        submissionsRepository.save(submission);

        return submission;
    }

    // Get Submission

    public Submissions getSubmissionById(long sid) throws IllegalArgumentException {
        if(submissionsRepository.findById(sid).isPresent()){
            return submissionsRepository.findById(sid).get();
        }
        else{
            throw new IllegalArgumentException("Cannot find submission with id " + sid);
        }
    }

    // Update Submission

    public Submissions updateSubmission(Submissions updatedValues, long sid) throws IllegalArgumentException {
        // Get Submission with Id
        Submissions submissionToUpdate = getSubmissionById(sid);

        // Update Submission values
        submissionToUpdate.updateValues(updatedValues);

        // Save Submission
        submissionsRepository.save(submissionToUpdate);

        // Return updated Submission
        return submissionToUpdate;
    }

    // Delete Submission

    public boolean deleteSubmissionById(long sid){
        if(submissionsRepository.findById(sid).isPresent()){
            submissionsRepository.deleteById(sid);
            return true;
        }
        else {
            return false;
        }
    }

    // List Submission

    public List<Submissions> getSubmissionListByChallenge(long cid) throws IllegalArgumentException {
        if(challengesRepository.findById(cid).isPresent()){
            Challenges challenge = challengesRepository.findById(cid).get();
            return getSubmissionListByChallenge(challenge);
        }
        else {
            throw new IllegalArgumentException("Cannot find challenge " + cid);
        }
    }

    public List<Submissions> getSubmissionListByChallenge(Challenges challenge){
        return challenge.getSubmissions();
    }

    public List<Submissions> getSubmissionListByAccount(long uid) throws IllegalArgumentException {
        Account account = accountService.getAccountById(uid);
        return getSubmissionListByAccount(account);
    }

    public List<Submissions> getSubmissionListByAccount(Account account){
        return account.getSubmissions();
    }


}

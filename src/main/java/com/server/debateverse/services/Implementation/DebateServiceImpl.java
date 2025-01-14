package com.server.debateverse.services.Implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.debateverse.entities.Debate;
import com.server.debateverse.entities.DebateReq;
import com.server.debateverse.entities.Option;
import com.server.debateverse.entities.User;
import com.server.debateverse.repositories.DebateRepo;
import com.server.debateverse.repositories.UserRepo;
import com.server.debateverse.services.DebateService;
import com.server.debateverse.services.OptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DebateServiceImpl implements DebateService {

    private final DebateRepo debateRepo;

    private final UserRepo userRepo;

    private final OptionService optionService;

    @Override
    public Debate createDebate(Debate debate, Long userId, List<Option> options) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("Debate" + debate);
        debate.setCreatedBy(user);
        debate.setPublic(true);
        Debate savedDebate = debateRepo.save(debate);
        for (Option option : options) {
            optionService.addOptionToDebate(option, savedDebate.getId());
        }
        return savedDebate;
    }

    @Override
    public List<Debate> getAllDebates() {
        return debateRepo.findAll();
    }

    @Override
    public List<Debate> getDebatesbyUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return debateRepo.findByCreatedBy(user);
    }

    @Override
    public List<Debate> getDebatesExceptUserDebates(Long userId) {
        return debateRepo.findAllExceptUserDebates(userId);
    }

    @Override
    public Object createBatchDebate(List<DebateReq> debateReqs, Long userId) {
        for (DebateReq debateReq : debateReqs) {
            createDebate(debateReq.getDebate(), userId, debateReq.getOptions());
        }
        return "Batch Debate Created";
    }

    @Override
    public Debate getDebateById(Long id) {
        return debateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Debate not found"));
    }

    @Override
    public void likeDebate(Long debateId, Long userId) {
        Debate debate = debateRepo.findById(debateId)
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Toggle like status
        if (debate.getLikes().contains(user)) {
            // If the user has already liked, remove them from the likes array
            debate.getLikes().remove(user);
        } else {
            // If the user hasn't liked yet, add them to the likes array
            debate.getLikes().add(user);
            // Remove from dislikes if previously disliked
            debate.getDislikes().remove(user);
        }

        // Save the updated debate
        debateRepo.save(debate);
    }

    @Override
    public void dislikeDebate(Long debateId, Long userId) {
        Debate debate = debateRepo.findById(debateId)
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Toggle dislike status
        if (debate.getDislikes().contains(user)) {
            // If the user has already disliked, remove them from the dislikes array
            debate.getDislikes().remove(user);
        } else {
            // If the user hasn't disliked yet, add them to the dislikes array
            debate.getDislikes().add(user);
            // Remove from likes if previously liked
            debate.getLikes().remove(user);
        }

        // Save the updated debate
        debateRepo.save(debate);
    }

    @Override
    public List<Debate> getLikedDebatesByUserId(Long userId) {
        return debateRepo.findLikedDebatesByUserId(userId);
    }

    @Override
    public List<Debate> getDislikedDebatesByUserId(Long userId) {
        return debateRepo.findDislikedDebatesByUserId(userId);
    }

    @Override
    public void blockDebate(Long debateId) {
        Debate debate = debateRepo.findById(debateId)
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));
        debate.setBlocked(true);
        debateRepo.save(debate);
    }

    @Override
    public void unblockDebate(Long debateId) {
        Debate debate = debateRepo.findById(debateId)
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));
        debate.setBlocked(false);
        debateRepo.save(debate);
    }

}

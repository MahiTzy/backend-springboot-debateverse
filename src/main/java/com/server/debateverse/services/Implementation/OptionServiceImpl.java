package com.server.debateverse.services.Implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.debateverse.entities.Debate;
import com.server.debateverse.entities.Option;
import com.server.debateverse.repositories.DebateRepo;
import com.server.debateverse.repositories.OptionRepo;
import com.server.debateverse.services.OptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final DebateRepo debateRepository;

    private final OptionRepo optionRepository;

    @Override
    public Option addOptionToDebate(Option option, Long debateId) {
        Debate debate = debateRepository.findById(debateId)
                .orElseThrow(() -> new RuntimeException("Debate not found"));
        option.setDebate(debate);
        return optionRepository.save(option);
    }

    @Override
    public List<Option> getOptionsForDebate(Long debateId) {
        Debate debate = debateRepository.findById(debateId)
                .orElseThrow(() -> new RuntimeException("Debate not found"));
        return optionRepository.findByDebate(debate);
    }

    @Override
    public void blockOption(Long optionId, Long debateId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        Debate debate = debateRepository.findById(debateId)
                .orElseThrow(() -> new RuntimeException("Debate not found"));

        if (debate.getOptions().contains(option)) {
            option.setBlocked(true);
            optionRepository.save(option);

            // Check if all options in the debate are blocked
            boolean allOptionsBlocked = debate.getOptions().stream()
                    .allMatch(Option::isBlocked);

            // Update the debate's public field if all options are blocked
            if (allOptionsBlocked) {
                debate.setPublic(false);
                debateRepository.save(debate);
            }
        } else {
            throw new RuntimeException("Option not found in debate");
        }
    }

    @Override
    public void unblockOption(Long optionId, Long debateId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));
        Debate debate = debateRepository.findById(debateId)
                .orElseThrow(() -> new RuntimeException("Debate not found"));
        if (debate.getOptions().contains(option)) {
            option.setBlocked(false);
            optionRepository.save(option);
        } else {
            throw new RuntimeException("Option not found in debate");
        }
    }
}

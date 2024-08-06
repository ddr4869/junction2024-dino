package dino.junction.domain.template.service;

import dino.junction.common.model.CustomException;
import dino.junction.domain.template.controller.request.TemplateCreateRequest;
import dino.junction.domain.template.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {
    private final TemplateRepository templateRepository;

    public Object templateServiceTest() {
        return "test";
    }

    public Object templateService404Test() throws BadRequestException{
        throw new BadRequestException("test");
    }

    public Object templateServicePostTest(TemplateCreateRequest req) throws BadRequestException{
        return "test";
    }
}

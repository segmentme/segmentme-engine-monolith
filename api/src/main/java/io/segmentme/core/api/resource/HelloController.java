package io.segmentme.core.api.resource;

import io.segmentme.core.service.dto.WorkspaceHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public WorkspaceHolder hello() {
        return new WorkspaceHolder().setName("asdad").setId("adsadavvvv");
    }
}

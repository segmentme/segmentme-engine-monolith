package io.segmentme.core.api.resource;

import io.segmentme.core.api.dto.context.ContextSchemaShortInfo;
import io.segmentme.core.api.facade.SdkFacade;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sdk")
public class SdkController {

    private final SdkFacade sdkFacade;


    @GetMapping("/connect")
    public IntegrationPoint connect(@RequestHeader("integration-point-key") String integrationPointKey) {
        return sdkFacade.connect(integrationPointKey);
    }

    @PostMapping("/actualize")
    public ContextSchemaShortInfo actualizeSchema(@RequestHeader("integration-point-key") String integrationPointKey, @RequestBody SchemaNode rootNode) {
        return sdkFacade.actualizeSchema(integrationPointKey, rootNode);
    }

}

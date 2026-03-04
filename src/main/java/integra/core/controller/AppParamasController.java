package integra.core.controller;

import integra.core.service.ParamsDataProxy;
import integra.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system-values")
@RequiredArgsConstructor
public class AppParamasController {
    private final ParamsDataProxy proxyParamsService;

    @RequestMapping()
    public ResponseEntity<ResponseData<?>> getValues() {
        return ResponseEntity.ok(ResponseData.of(proxyParamsService.getParams(), "Variables de sistema"));
    }
}

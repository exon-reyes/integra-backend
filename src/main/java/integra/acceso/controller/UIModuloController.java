package integra.acceso.controller;

import integra.acceso.dto.UINode;
import integra.acceso.service.uinode.UINodeService;
import integra.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("ui-node")
@RequiredArgsConstructor
public class UIModuloController {

    private final UINodeService UINodeService;

    @GetMapping("tree")
    public ResponseEntity<ResponseData<List<UINode>>> getFullTree() {
        return ResponseEntity.ok(ResponseData.success("Catálogo jerárquico recuperado", UINodeService.getPermissionTree()));
    }
}
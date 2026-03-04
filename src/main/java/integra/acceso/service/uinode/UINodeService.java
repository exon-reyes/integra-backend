package integra.acceso.service.uinode;

import integra.acceso.dto.UINode;
import integra.acceso.entity.SecurityNode;
import integra.acceso.repository.SecurityNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UINodeService {

    private final SecurityNodeRepository nodeRepository;

    @Transactional(readOnly = true)
    public List<UINode> getPermissionTree() {
        List<SecurityNode> allNodes = nodeRepository.findAll();
        Map<String, List<SecurityNode>> nodesByParent = allNodes.stream()
                .collect(Collectors.groupingBy(node ->
                        node.getParentId() != null ? node.getParentId() : "ROOT"));

        return nodesByParent.get("ROOT").stream()
                .sorted(Comparator.comparing(SecurityNode::getOrden))
                .map(node -> buildNodeTree(node, nodesByParent))
                .toList();
    }

    private UINode buildNodeTree(SecurityNode node, Map<String, List<SecurityNode>> nodesByParent) {
        UINode dto = new UINode();
        dto.setId(node.getId());
        dto.setName(node.getName());
        dto.setType(node.getType().name());
        dto.setParentId(node.getParentId());
        dto.setOrden(node.getOrden());

        List<SecurityNode> children = nodesByParent.getOrDefault(node.getId(), List.of());
        dto.setChildren(children.stream()
                .sorted(Comparator.comparing(SecurityNode::getOrden))
                .map(child -> buildNodeTree(child, nodesByParent))
                .toList());

        return dto;
    }
}
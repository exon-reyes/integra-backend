package integra.acceso.dto;

import lombok.Data;

import java.util.List;

@Data
public class UINode {
    private String id;
    private String name;
    private String type;
    private String parentId;
    private int orden;
    private List<UINode> children;
}
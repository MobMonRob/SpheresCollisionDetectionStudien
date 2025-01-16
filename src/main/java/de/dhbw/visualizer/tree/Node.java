package de.dhbw.visualizer.tree;

import de.dhbw.visualizer.Sphere;

public record Node(
        Sphere sphere,
        Node left,
        Node right
) {

    /*
        - s: []
          l:
          - s:[]
            l:
              - []
     */
    public String drawTree() {
        return drawTree(0);
    }

    private String drawTree(int ident) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("- ")
                .append(" ".repeat(ident))
                .append(" s:")
                .append(sphere)
                .append(System.lineSeparator());

        if (left != null) {
            stringBuilder.append(" ".repeat(ident + 2))
                    .append("l:").append(System.lineSeparator());
            stringBuilder.append(left.drawTree(ident + 2));
        }
        if (right != null) {
            stringBuilder.append(" ".repeat(ident + 2))
                    .append("l:").append(System.lineSeparator());
            stringBuilder.append(right.drawTree(ident + 2));
        }
        return stringBuilder.toString();
    }
}

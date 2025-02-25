package de.dhbw.visualizer.collision;

import java.util.List;

public record SelfCollisionResult(Status status, List<CollisionEntry> collidedLinks) {

    public static SelfCollisionResult fromStatusList(List<CollisionEntry> collidedLinks) {
        return new SelfCollisionResult(collidedLinks.isEmpty() ? Status.OK : Status.COLLISION, collidedLinks);
    }

    public boolean isOk() {
        return this.status == Status.OK;
    }

    public boolean isError() {
        return this.status == Status.COLLISION;
    }

    public enum Status {
        OK,
        COLLISION
    }

    public record CollisionEntry(String link1, String link2) {
    }
}

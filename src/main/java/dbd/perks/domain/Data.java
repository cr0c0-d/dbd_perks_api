package dbd.perks.domain;


import java.time.LocalDateTime;

public interface Data {
    Long id = null;

    String name = null;

    String enName = null;

    Long ver = null;

    LocalDateTime lastModified = null;

    Boolean isActivated = null;

    LocalDateTime createdAt = null;

    Boolean equals(Data data);

    void deactivate();

    String getEnName();

    Boolean validate();
}

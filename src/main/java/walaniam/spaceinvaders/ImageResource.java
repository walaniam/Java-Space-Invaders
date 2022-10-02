package walaniam.spaceinvaders;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.net.URL;
import java.util.Optional;

@RequiredArgsConstructor
@ToString
public enum ImageResource {

    ALIEN("/images/alien.png"),
    BOMB("/images/bomb.png"),
    EXPLOSION("/images/explosion.png"),
    PLAYER("/images/player.png"),
    SHOT("/images/shot.png");

    private final String location;

    public URL getUrl() {
        return Optional
                .ofNullable(this.getClass().getResource(location))
                .orElseThrow(() -> new IllegalStateException("Image not found: " + location));
    }
}

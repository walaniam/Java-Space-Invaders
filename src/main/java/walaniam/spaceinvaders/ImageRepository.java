package walaniam.spaceinvaders;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class ImageRepository {

    public static final ImageRepository INSTANCE = new ImageRepository();

    private final Map<ImageResource, Image> images;

    private ImageRepository() {
        this.images = Arrays.stream(ImageResource.values()).collect(toMap(
                Function.identity(),
                ImageRepository::loadImage
        ));
    }

    private static Image loadImage(ImageResource image) {
        log.debug("Loading image: {}", image);
        var icon = new ImageIcon(image.getUrl());
        return icon.getImage();
    }

    public Image getImage(ImageResource resource) {
        return images.get(resource);
    }
}

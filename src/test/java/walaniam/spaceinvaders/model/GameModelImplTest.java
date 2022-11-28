package walaniam.spaceinvaders.model;

import com.zetcode.sprite.Sprite;
import org.junit.jupiter.api.Test;

import static com.zetcode.Commons.NUMBER_OF_ALIENS_TO_DESTROY;
import static org.assertj.core.api.Assertions.assertThat;

class GameModelImplTest {

    @Test
    void shouldCalculateDiedAliensWhenMerge() {
        // given
        var model1 = new GameModelImpl();
        var model2 = new GameModelImpl();
        model1.getAliens().get(0).die();
        model1.getAliens().get(2).die();
        model1.getAliens().get(4).die();
        model2.getAliens().get(11).die();
        model2.getAliens().get(13).die();
        model2.getAliens().get(15).die();
        long model1VisibleAliens = model1.getAliens().stream()
                .filter(Sprite::isVisible)
                .count();
        long model2VisibleAliens = model2.getAliens().stream()
                .filter(Sprite::isVisible)
                .count();
        assertThat(model1VisibleAliens).isEqualTo(NUMBER_OF_ALIENS_TO_DESTROY - 3);
        assertThat(model2VisibleAliens).isEqualTo(NUMBER_OF_ALIENS_TO_DESTROY - 3);

        // when
        model1.mergeWith(model2);

        // then
        long model1VisibleAliensAfterMerge = model1.getAliens().stream()
                .filter(Sprite::isVisible)
                .count();
        assertThat(model1VisibleAliensAfterMerge).isEqualTo(NUMBER_OF_ALIENS_TO_DESTROY - 6);
        assertThat(model1.getDeaths()).isEqualTo(6);
    }
}
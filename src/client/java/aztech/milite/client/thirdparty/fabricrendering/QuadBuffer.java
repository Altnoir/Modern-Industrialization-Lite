package aztech.milite.client.thirdparty.fabricrendering;

public class QuadBuffer extends MutableQuadViewImpl {
    {
        data = new int[EncodingFormat.TOTAL_STRIDE];
        clear();
    }

    @Override
    public void emitDirectly() {}
}

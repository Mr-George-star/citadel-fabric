package net.george.citadel.client.render;

import net.george.citadel.server.block.CitadelLecternBlockEntity;
import net.george.citadel.server.block.LecternBooks;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

public class CitadelLecternRenderer implements BlockEntityRenderer<CitadelLecternBlockEntity> {
    private final BookModel bookModel;
    public static final Identifier BOOK_PAGE_TEXTURE =  new Identifier("citadel:textures/entity/lectern_book_pages.png");
    public static final Identifier BOOK_BINDING_TEXTURE = new Identifier("citadel:textures/entity/lectern_book_binding.png");
    private static final LecternBooks.BookData EMPTY_BOOK_DATA = new LecternBooks.BookData(0XC58439, 0XF4E9BF);

    public CitadelLecternRenderer(BlockEntityRendererFactory.Context context) {
        this.bookModel = new BookModel(context.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(CitadelLecternBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int i, int j) {
        BlockState state = blockEntity.getCachedState();
        if (state.get(LecternBlock.HAS_BOOK)) {
            LecternBooks.BookData bookData = LecternBooks.BOOKS.getOrDefault(Registry.ITEM.getId(blockEntity.getBook().getItem()), EMPTY_BOOK_DATA);
            matrices.push();
            matrices.translate(0.5D, 1.0625D, 0.5D);
            float f = state.get(LecternBlock.FACING).rotateYClockwise().asRotation();
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(67.5F));
            matrices.translate(0.0D, -0.125D, 0.0D);
            this.bookModel.setPageAngles(0.0F, 0.1F, 0.9F, 1.2F);
            int bindingR = (bookData.getBindingColor() & 0xFF0000) >> 16;
            int bindingG = (bookData.getBindingColor() & 0xFF00) >> 8;
            int bindingB = (bookData.getBindingColor() & 0xFF);
            int pageR = (bookData.getPageColor() & 0xFF0000) >> 16;
            int pageG = (bookData.getPageColor() & 0xFF00) >> 8;
            int pageB = (bookData.getPageColor() & 0xFF);
            VertexConsumer pages = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(BOOK_PAGE_TEXTURE));
            this.bookModel.renderBook(matrices, pages, i, j, pageR / 255F, pageG / 255F, pageB / 255F, 1.0F);
            VertexConsumer binding = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(BOOK_BINDING_TEXTURE));
            this.bookModel.renderBook(matrices, binding, i, j, bindingR / 255F, bindingG / 255F, bindingB / 255F, 1.0F);
            matrices.pop();
        }
    }
}

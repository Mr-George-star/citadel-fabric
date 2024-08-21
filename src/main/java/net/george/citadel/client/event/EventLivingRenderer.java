package net.george.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.CitadelEventBase;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@SuppressWarnings("unused")
public abstract class EventLivingRenderer implements CitadelEventBase {
    public static final Event<SetupRotations.Context> SETUP_ROTATIONS = EventFactory.createArrayBacked(SetupRotations.Context.class,
            (listeners) -> event -> {
                for (SetupRotations.Context callback : listeners) {
                    callback.interact(event);
                }
            });
    public static final Event<AccessToBufferSource.Context> ACCESS_TO_BUFFER_SOURCE = EventFactory.createArrayBacked(AccessToBufferSource.Context.class,
            (listeners) -> event -> {
                for (AccessToBufferSource.Context callback : listeners) {
                    callback.interact(event);
                }
            });
    public static final Event<PreSetupAnimations.Context> PRE_SETUP_ANIMATIONS = EventFactory.createArrayBacked(PreSetupAnimations.Context.class,
            (listeners) -> event -> {
                for (PreSetupAnimations.Context callback : listeners) {
                    callback.interact(event);
                }
            });
    public static final Event<PostSetupAnimations.Context> POST_SETUP_ANIMATIONS = EventFactory.createArrayBacked(PostSetupAnimations.Context.class,
            (listeners) -> event -> {
                for (PostSetupAnimations.Context callback : listeners) {
                    callback.interact(event);
                }
            });
    public static final Event<PostRenderModel.Context> POST_RENDER_MODEL = EventFactory.createArrayBacked(PostRenderModel.Context.class,
            (listeners) -> event -> {
                for (PostRenderModel.Context callback : listeners) {
                    callback.interact(event);
                }
            });
    private final LivingEntity entity;
    private final EntityModel<?> model;
    private final MatrixStack matrices;
    private final float tickDelta;

    public EventLivingRenderer(LivingEntity entity, EntityModel<?> model, MatrixStack matrices, float tickDelta) {
        this.entity = entity;
        this.model = model;
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public EntityModel<?> getModel() {
        return this.model;
    }

    public MatrixStack getMatrices() {
        return this.matrices;
    }

    public float getTickDelta() {
        return this.tickDelta;
    }

    @Override
    public abstract void interact();

    public static class SetupRotations extends EventLivingRenderer {
        private final float bodyYaw;

        public SetupRotations(LivingEntity entity, EntityModel<?> model, MatrixStack matrices, float tickDelta, float bodyYaw) {
            super(entity, model, matrices, tickDelta);
            this.bodyYaw = bodyYaw;
        }

        public float getBodyYaw() {
            return this.bodyYaw;
        }

        @Override
        public void interact() {
            SETUP_ROTATIONS.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(SetupRotations event);
        }
    }

    public static class AccessToBufferSource extends EventLivingRenderer {
        private final float bodyYaw;
        private final VertexConsumerProvider vertices;
        private final int light;

        public AccessToBufferSource(LivingEntity entity, EntityModel<?> model, MatrixStack matrices, float bodyYaw, float tickDelta, VertexConsumerProvider vertices, int light) {
            super(entity, model, matrices, tickDelta);
            this.bodyYaw = bodyYaw;
            this.vertices = vertices;
            this.light = light;
        }

        public float getBodyYaw() {
            return this.bodyYaw;
        }

        public VertexConsumerProvider getVertices() {
            return this.vertices;
        }

        public int getLight() {
            return this.light;
        }

        @Override
        public void interact() {
            ACCESS_TO_BUFFER_SOURCE.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(AccessToBufferSource event);
        }
    }

    public static class PreSetupAnimations extends AccessToBufferSource {
        public PreSetupAnimations(LivingEntity entity, EntityModel<?> model, MatrixStack matrices, float bodyYaw, float tickDelta, VertexConsumerProvider vertices, int light) {
            super(entity, model, matrices, bodyYaw, tickDelta, vertices, light);
        }

        @Override
        public void interact() {
            PRE_SETUP_ANIMATIONS.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(PreSetupAnimations event);
        }
    }

    public static class PostSetupAnimations extends AccessToBufferSource {
        public PostSetupAnimations(LivingEntity entity, EntityModel<?> model, MatrixStack matrices, float bodyYaw, float tickDelta, VertexConsumerProvider vertices, int light) {
            super(entity, model, matrices, bodyYaw, tickDelta, vertices, light);
        }

        @Override
        public void interact() {
            POST_SETUP_ANIMATIONS.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(PostSetupAnimations event);
        }
    }

    public static class PostRenderModel extends AccessToBufferSource {
        public PostRenderModel(LivingEntity entity, EntityModel<?> model, MatrixStack matrices, float bodyYaw, float tickDelta, VertexConsumerProvider vertices, int light) {
            super(entity, model, matrices, bodyYaw, tickDelta, vertices, light);
        }

        @Override
        public void interact() {
            POST_RENDER_MODEL.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(PostRenderModel event);
        }
    }
}

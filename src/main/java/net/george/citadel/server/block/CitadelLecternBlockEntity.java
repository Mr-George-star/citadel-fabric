package net.george.citadel.server.block;

import net.george.citadel.Citadel;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class CitadelLecternBlockEntity extends BlockEntity implements Clearable, NamedScreenHandlerFactory {
    private ItemStack book;
    private final Inventory bookAccess;
    private final PropertyDelegate dataAccess;

    public CitadelLecternBlockEntity(BlockPos pos, BlockState state) {
        super(Citadel.LECTERN_BLOCK_ENTITY.get(), pos, state);
        this.book = ItemStack.EMPTY;
        this.bookAccess = new Inventory() {
            @Override
            public int size() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return CitadelLecternBlockEntity.this.book.isEmpty();
            }

            @Override
            public ItemStack getStack(int i) {
                return i == 0 ? CitadelLecternBlockEntity.this.book : ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeStack(int i, int j) {
                if (i == 0) {
                    ItemStack itemstack = CitadelLecternBlockEntity.this.book.split(j);
                    if (CitadelLecternBlockEntity.this.book.isEmpty()) {
                        CitadelLecternBlockEntity.this.onBookItemRemove();
                    }

                    return itemstack;
                } else {
                    return ItemStack.EMPTY;
                }
            }

            @Override
            public ItemStack removeStack(int i) {
                if (i == 0) {
                    ItemStack book = CitadelLecternBlockEntity.this.book;
                    CitadelLecternBlockEntity.this.book = ItemStack.EMPTY;
                    CitadelLecternBlockEntity.this.onBookItemRemove();
                    return book;
                } else {
                    return ItemStack.EMPTY;
                }
            }

            @Override
            public void setStack(int i, ItemStack stack) {
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public void markDirty() {
                CitadelLecternBlockEntity.this.markDirty();
            }

            @Override
            public boolean canPlayerUse(PlayerEntity entity) {
                if (CitadelLecternBlockEntity.this.world.getBlockEntity(CitadelLecternBlockEntity.this.pos) != CitadelLecternBlockEntity.this) {
                    return false;
                } else {
                    return !(entity.squaredDistanceTo((double) CitadelLecternBlockEntity.this.pos.getX() + 0.5, (double) CitadelLecternBlockEntity.this.pos.getY() + 0.5, (double) CitadelLecternBlockEntity.this.pos.getZ() + 0.5) > 64.0) && CitadelLecternBlockEntity.this.hasBook();
                }
            }

            @Override
            public boolean isValid(int i, ItemStack stack) {
                return false;
            }

            @Override
            public void clear() {
            }
        };
        this.dataAccess = new PropertyDelegate() {
            @Override
            public int get(int i) {
                return 0;
            }

            @Override
            public void set(int i, int j) {
            }

            @Override
            public int size() {
                return 1;
            }
        };
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean hasBook() {
        return LecternBooks.isLecternBook(this.book);
    }

    public void setBook(ItemStack stack) {
        this.setBook(stack, null);
    }

    void onBookItemRemove() {
        LecternBlock.setHasBook(this.getWorld(), this.getPos(), this.getCachedState(), false);
    }

    public void setBook(ItemStack itemStack, @Nullable PlayerEntity player) {
        this.book = itemStack;
        this.markDirty();
    }

    public int getRedstoneSignal() {
        return this.hasBook() ? 1 : 0;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Book", 10)) {
            this.book = ItemStack.fromNbt(nbt.getCompound("Book"));
        } else {
            this.book = ItemStack.EMPTY;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.getBook().isEmpty()) {
            nbt.put("Book", this.getBook().writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void clear() {
        this.setBook(ItemStack.EMPTY);
    }

    @Override
    public ScreenHandler createMenu(int i, PlayerInventory inventory, PlayerEntity player) {
        return new LecternScreenHandler(i, this.bookAccess, this.dataAccess);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.lectern");
    }
}

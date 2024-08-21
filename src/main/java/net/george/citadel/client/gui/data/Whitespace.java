package net.george.citadel.client.gui.data;

@SuppressWarnings("unused")
public class Whitespace {
    private final int page;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private boolean down = false;

    public Whitespace(int page, int x, int y, int width, int height) {
        this.page = page;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Whitespace(int page, int x, int y, int width, int height, boolean down) {
        this.page = page;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.down = down;
    }

    public int getPage() {
        return this.page;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isDown() {
        return this.down;
    }

    public void setDown(boolean down){
        this.down = down;
    }

    @Override
    public boolean equals(Object other){
        if (other instanceof Whitespace whitespace){
            return whitespace.x == this.x && whitespace.y == this.y && whitespace.height == this.height && whitespace.width == this.width && whitespace.down == this.down;
        }
        return false;
    }
}

package net.george.citadel.client.gui.data;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class RecipeData {
    private String recipe;
    private final boolean shapeless;
    private int x;
    private int y;
    private double scale;
    private final int page;

    public RecipeData(String recipe, int x, int y, boolean shapeless, double scale, int page) {
        this.recipe = recipe;
        this.x = x;
        this.y = y;
        this.shapeless = shapeless;
        this.scale = scale;
        this.page = page;
    }

    public String getRecipe() {
        return this.recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public int getPage() {
        return this.page;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}

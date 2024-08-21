package net.george.citadel.client.gui;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

@SuppressWarnings("unused")
public class ForgeSlider extends SliderWidget {
    protected Text prefix;
    protected Text suffix;
    protected double minValue;
    protected double maxValue;
    protected double stepSize;
    protected boolean drawString;
    private final DecimalFormat format;

    public ForgeSlider(int x, int y, int width, int height, Text prefix, Text suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, Text.empty(), 0.0);
        this.prefix = prefix;
        this.suffix = suffix;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = Math.abs(stepSize);
        this.value = this.snapToNearest((currentValue - minValue) / (maxValue - minValue));
        this.drawString = drawString;
        if (stepSize == 0.0) {
            precision = Math.min(precision, 4);
            StringBuilder builder = new StringBuilder("0");
            if (precision > 0) {
                builder.append('.');
            }

            while(precision-- > 0) {
                builder.append('0');
            }

            this.format = new DecimalFormat(builder.toString());
        } else if (MathHelper.approximatelyEquals(this.stepSize, Math.floor(this.stepSize))) {
            this.format = new DecimalFormat("0");
        } else {
            this.format = new DecimalFormat(Double.toString(this.stepSize).replaceAll("\\d", "0"));
        }

        this.updateMessage();
    }

    public ForgeSlider(int x, int y, int width, int height, Text prefix, Text suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1.0, 0, drawString);
    }

    public double getValue() {
        return this.value * (this.maxValue - this.minValue) + this.minValue;
    }

    public long getValueLong() {
        return Math.round(this.getValue());
    }

    public int getValueInt() {
        return (int)this.getValueLong();
    }

    public void setValue(double value) {
        this.value = this.snapToNearest((value - this.minValue) / (this.maxValue - this.minValue));
        this.updateMessage();
    }

    public String getValueString() {
        return this.format.format(this.getValue());
    }

    public void onClick(double mouseX, double mouseY) {
        this.setValueFromMouse(mouseX);
    }

    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);
        this.setValueFromMouse(mouseX);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean flag = keyCode == 263;
        if (flag || keyCode == 262) {
            if (this.minValue > this.maxValue) {
                flag = !flag;
            }

            float f = flag ? -1.0F : 1.0F;
            if (this.stepSize <= 0.0) {
                this.setSliderValue(this.value + (double)(f / (float)(this.width - 8)));
            } else {
                this.setValue(this.getValue() + (double)f * this.stepSize);
            }
        }

        return false;
    }

    private void setValueFromMouse(double mouseX) {
        this.setSliderValue((mouseX - (double)(this.x + 4)) / (double)(this.width - 8));
    }

    private void setSliderValue(double value) {
        double oldValue = this.value;
        this.value = this.snapToNearest(value);
        if (!MathHelper.approximatelyEquals(oldValue, this.value)) {
            this.applyValue();
        }

        this.updateMessage();
    }

    private double snapToNearest(double value) {
        if (this.stepSize <= 0.0) {
            return MathHelper.clamp(value, 0.0, 1.0);
        } else {
            value = MathHelper.lerp(MathHelper.clamp(value, 0.0, 1.0), this.minValue, this.maxValue);
            value = this.stepSize * (double)Math.round(value / this.stepSize);
            if (this.minValue > this.maxValue) {
                value = MathHelper.clamp(value, this.maxValue, this.minValue);
            } else {
                value = MathHelper.clamp(value, this.minValue, this.maxValue);
            }

            return MathHelper.map(value, this.minValue, this.maxValue, 0.0, 1.0);
        }
    }

    protected void updateMessage() {
        if (this.drawString) {
            this.setMessage(Text.literal("").append(this.prefix).append(this.getValueString()).append(this.suffix));
        } else {
            this.setMessage(Text.empty());
        }

    }

    protected void applyValue() {
    }
}

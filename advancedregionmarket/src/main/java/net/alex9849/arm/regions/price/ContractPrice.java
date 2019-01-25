package net.alex9849.arm.regions.price;

import net.alex9849.arm.regions.price.Autoprice.AutoPrice;

public class ContractPrice extends Price {
    protected long extendTime;

    public ContractPrice(double price, long extendTime) {
        super(price);
        if(extendTime < 1000) {
            this.extendTime = 1000;
        } else {
            this.extendTime = extendTime;
        }
    }

    public ContractPrice(AutoPrice autoPrice) {
        super(autoPrice);
        this.extendTime = autoPrice.getExtendtime();
    }

    public long getExtendTime() {
        return this.extendTime;
    }
}
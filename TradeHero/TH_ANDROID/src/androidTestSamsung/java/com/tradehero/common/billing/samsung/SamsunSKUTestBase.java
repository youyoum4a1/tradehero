package com.tradehero.common.billing.samsung;

public class SamsunSKUTestBase extends SamsungItemGroupTestBase
{
    protected SamsungSKU createSKU1()
    {
        return new SamsungSKU("abc1", "def1");
    }

    protected SamsungSKU createSKU2()
    {
        return new SamsungSKU("abc1", "def2");
    }

    protected SamsungSKU createSKU3()
    {
        return new SamsungSKU("abc2", "def1");
    }

    protected SamsungSKU createSKU4()
    {
        return new SamsungSKU("abc2", "def2");
    }
}

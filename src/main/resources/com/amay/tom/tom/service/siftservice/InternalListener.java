package com.amay.tom.service.siftservice;

public interface InternalListener {

    public void PauseShift();
    public void EOShift();
    public  void ResumeShift(char[] password);
}

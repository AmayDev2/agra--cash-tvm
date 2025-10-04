package com.amay.tvm.backend.service;

public interface BnrFinanceMaintenance {
    void bnrLoad();
    void bnrLoadRollback();
    void bnrLoadCommit();
    void bnrUnload();
    int bnrUnload(String rcyId);
    void cancelTimeout();
    int bnrUnloadRecycler();
    Object bnrModuleStatus();
}

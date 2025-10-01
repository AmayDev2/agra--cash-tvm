package com.amay.tvm.backend.service;

public interface BnrFinanceMaintenance {
    void bnrLoad();
    void bnrLoadRollback();
    void bnrLoadCommit();
    void bnrUnload();
    void bnrUnloadRecycler(String rcyId);
    Object bnrModuleStatus();
}

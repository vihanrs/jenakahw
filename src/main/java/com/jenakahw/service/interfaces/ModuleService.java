package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Module;

public interface ModuleService {

    List<Module> findAll();

    List<Module> getModulesByRoleWithoutPrivileges(Integer roleId);

    String[] getModulesByLoggedUser();
}

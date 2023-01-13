package com.nekozouneko.anni.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class BooleanDataType implements PersistentDataType<Byte, Boolean> {
    @NotNull
    @Override
    public Class<Byte> getPrimitiveType() {
        return Byte.class;
    }

    @NotNull
    @Override
    public Class<Boolean> getComplexType() {
        return Boolean.class;
    }

    @NotNull
    @Override
    public Byte toPrimitive(@NotNull Boolean b, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        if (b == null || !b) return 0;
        else return 1;
    }

    @NotNull
    @Override
    public Boolean fromPrimitive(@NotNull Byte b, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        if (b == 1) return true;
        return false;
    }
}

这个是修复demo 方法的原smali
.method public static PreLoadNativeSO(Landroid/content/Context;Ljava/lang/String;)V
    .registers 5
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "source"    # Ljava/lang/String;

    .line 34
    :try_start_0
    const-string v0, "arm64-v8a"

    .line 35
    .local v0, "abi":Ljava/lang/String;
    invoke-static {}, Landroid/os/Process;->is64Bit()Z

    move-result v1

    if-nez v1, :cond_b

    .line 36
    const-string v1, "armeabi-v7a"

    move-object v0, v1

    .line 38
    :cond_b
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, "!/lib/"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, "/libDavilkRuntime.so"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    .line 39
    .local v1, "libdump":Ljava/lang/String;
    invoke-static {v1}, Ljava/lang/System;->load(Ljava/lang/String;)V
    :try_end_2b
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_2b} :catch_2c

    .line 42
    .end local v0    # "abi":Ljava/lang/String;
    .end local v1    # "libdump":Ljava/lang/String;
    goto :goto_34

    .line 40
    :catch_2c
    move-exception v0

    .line 41
    .local v0, "e":Ljava/lang/Exception;
    const-string v1, "LoadEntry"

    const-string v2, "LoadSo error"

    invoke-static {v1, v2}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 43
    .end local v0    # "e":Ljava/lang/Exception;
    :goto_34
    return-void
.end method
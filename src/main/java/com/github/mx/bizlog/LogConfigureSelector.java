package com.github.mx.bizlog;

import com.github.mx.bizlog.annotation.EnableLog;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;

/**
 * Create by max on 2021/02/27
 **/
public class LogConfigureSelector extends AdviceModeImportSelector<EnableLog> {

    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        return new String[]{LogProxyAutoConfiguration.class.getName()};
    }
}
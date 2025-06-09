package com.utpintegrador.semana10;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Semana10 {

    // Logger de SLF4J con implementación Logback
    
    private static final Logger logger = LoggerFactory.getLogger(Semana10.class);
    
    public static void main(String[] args) {
      logger.info("PROBANDO LAS 4 LIBRERIAS");
      
      // LOGBACK
      logger.error(" Logback ERROR funciona");
      logger.warn(" Logback WARN funciona");
      logger.info(" Logback INFO funciona");
      
      //GUAVA
      var lista = Lists.newArrayList("Juan", "Maria", "Carlos");
      logger.info(" Guava creo lista {}", lista);
      
      //Apache COMMONS
      String nombre = StringUtils.capitalize("juan perez");
      logger.info(" Commons limpio nombre: {}", nombre);
      
      logger.info(" TODAS LAS LIBRERIAS FUNCIONAN!");
    }
}

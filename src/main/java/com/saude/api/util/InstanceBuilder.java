package com.saude.api.util;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Map;

public class InstanceBuilder {

    /**
     * Constroi uma Instance do Weka baseada no cabecalho ARFF e num Map de atributos.
     *
     * @param header Cabecalho vazio do dataset ARFF do modelo alvo.
     * @param attributes Map contendo chave=NomeDoAtributo, valor=Valor.
     * @return Instance configurada pronta para predicao.
     */
    public static Instance buildInstance(Instances header, Map<String, Object> attributes) {
        Instance instance = new DenseInstance(header.numAttributes());
        instance.setDataset(header);

        for (int i = 0; i < header.numAttributes(); i++) {
            Attribute attr = header.attribute(i);
            
            // Ignorar o preenchimento do atributo que e o alvo da predicao (classe)
            if (attr.name().equals(header.classAttribute().name())) {
                instance.setMissing(attr);
                continue;
            }

            Object value = attributes.get(attr.name());

            if (value == null) {
                instance.setMissing(attr);
                continue;
            }

            if (attr.isNumeric()) {
                if (value instanceof Number) {
                    instance.setValue(attr, ((Number) value).doubleValue());
                } else {
                    try {
                        instance.setValue(attr, Double.parseDouble(value.toString()));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("O atributo '" + attr.name() + "' deve ser numerico. Valor recebido: " + value);
                    }
                }
            } else if (attr.isNominal() || attr.isString()) {
                String strValue = value.toString();
                if (attr.isNominal() && attr.indexOfValue(strValue) == -1) {
                    throw new IllegalArgumentException("Valor '" + strValue + "' nao e valido para o atributo '" + attr.name() + "'. Opcoes validas: " + getNominalValues(attr));
                }
                instance.setValue(attr, strValue);
            }
        }

        return instance;
    }

    private static String getNominalValues(Attribute attr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < attr.numValues(); i++) {
            sb.append(attr.value(i));
            if (i < attr.numValues() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}

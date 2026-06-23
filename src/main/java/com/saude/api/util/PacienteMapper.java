package com.saude.api.util;

import com.saude.api.dto.*;
import com.saude.api.entity.*;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper {

    public PacienteDTO toDTO(Paciente entity) {
        if (entity == null) return null;

        PacienteDTO dto = PacienteDTO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .cpf(entity.getCpf())
                .cns(entity.getCns())
                .dataNascimento(entity.getDataNascimento())
                .sexo(entity.getSexo())
                .gestante(entity.getGestante())
                .puerpera(entity.getPuerpera())
                .raca(entity.getRaca())
                .estadoCivil(entity.getEstadoCivil())
                .escolaridade(entity.getEscolaridade())
                .profissao(entity.getProfissao())
                .build();

        if (entity.getEndereco() != null) {
            dto.setEndereco(EnderecoDTO.builder()
                    .cep(entity.getEndereco().getCep())
                    .logradouro(entity.getEndereco().getLogradouro())
                    .numero(entity.getEndereco().getNumero())
                    .complemento(entity.getEndereco().getComplemento())
                    .bairro(entity.getEndereco().getBairro())
                    .municipio(entity.getEndereco().getMunicipio())
                    .zona(entity.getEndereco().getZona())
                    .uf(entity.getEndereco().getUf())
                    .build());
        }

        if (entity.getContato() != null) {
            dto.setContato(ContatoDTO.builder()
                    .telefone(entity.getContato().getTelefone())
                    .celular(entity.getContato().getCelular())
                    .email(entity.getContato().getEmail())
                    .contatoEmergencia(entity.getContato().getContatoEmergencia())
                    .telefoneEmergencia(entity.getContato().getTelefoneEmergencia())
                    .build());
        }

        if (entity.getComorbidade() != null) {
            dto.setComorbidade(ComorbidadeDTO.builder()
                    .fatorRisco(entity.getComorbidade().getFatorRisco())
                    .diabetes(entity.getComorbidade().getDiabetes())
                    .cardiopatia(entity.getComorbidade().getCardiopatia())
                    .asma(entity.getComorbidade().getAsma())
                    .obesidade(entity.getComorbidade().getObesidade())
                    .renal(entity.getComorbidade().getRenal())
                    .hepatica(entity.getComorbidade().getHepatica())
                    .pneumopatia(entity.getComorbidade().getPneumopatia())
                    .hematologica(entity.getComorbidade().getHematologica())
                    .neurologica(entity.getComorbidade().getNeurologica())
                    .imunodepressao(entity.getComorbidade().getImunodepressao())
                    .sindromeDown(entity.getComorbidade().getSindromeDown())
                    .build());
        }

        if (entity.getVacinacao() != null) {
            dto.setVacinacao(VacinacaoDTO.builder()
                    .vacinaCovid(entity.getVacinacao().getVacinaCovid())
                    .primeiraDose(entity.getVacinacao().getPrimeiraDose())
                    .segundaDose(entity.getVacinacao().getSegundaDose())
                    .terceiraDose(entity.getVacinacao().getTerceiraDose())
                    .vacinaInfluenza(entity.getVacinacao().getVacinaInfluenza())
                    .build());
        }

        return dto;
    }

    public Paciente toEntity(PacienteDTO dto) {
        if (dto == null) return null;

        Paciente entity = Paciente.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .cns(dto.getCns())
                .dataNascimento(dto.getDataNascimento())
                .sexo(dto.getSexo())
                .gestante(dto.getGestante())
                .puerpera(dto.getPuerpera())
                .raca(dto.getRaca())
                .estadoCivil(dto.getEstadoCivil())
                .escolaridade(dto.getEscolaridade())
                .profissao(dto.getProfissao())
                .build();

        if (dto.getEndereco() != null) {
            entity.setEndereco(Endereco.builder()
                    .cep(dto.getEndereco().getCep())
                    .logradouro(dto.getEndereco().getLogradouro())
                    .numero(dto.getEndereco().getNumero())
                    .complemento(dto.getEndereco().getComplemento())
                    .bairro(dto.getEndereco().getBairro())
                    .municipio(dto.getEndereco().getMunicipio())
                    .zona(dto.getEndereco().getZona())
                    .uf(dto.getEndereco().getUf())
                    .build());
        }

        if (dto.getContato() != null) {
            entity.setContato(Contato.builder()
                    .telefone(dto.getContato().getTelefone())
                    .celular(dto.getContato().getCelular())
                    .email(dto.getContato().getEmail())
                    .contatoEmergencia(dto.getContato().getContatoEmergencia())
                    .telefoneEmergencia(dto.getContato().getTelefoneEmergencia())
                    .build());
        }

        if (dto.getComorbidade() != null) {
            entity.setComorbidade(Comorbidade.builder()
                    .fatorRisco(dto.getComorbidade().getFatorRisco())
                    .diabetes(dto.getComorbidade().getDiabetes())
                    .cardiopatia(dto.getComorbidade().getCardiopatia())
                    .asma(dto.getComorbidade().getAsma())
                    .obesidade(dto.getComorbidade().getObesidade())
                    .renal(dto.getComorbidade().getRenal())
                    .hepatica(dto.getComorbidade().getHepatica())
                    .pneumopatia(dto.getComorbidade().getPneumopatia())
                    .hematologica(dto.getComorbidade().getHematologica())
                    .neurologica(dto.getComorbidade().getNeurologica())
                    .imunodepressao(dto.getComorbidade().getImunodepressao())
                    .sindromeDown(dto.getComorbidade().getSindromeDown())
                    .build());
        }

        if (dto.getVacinacao() != null) {
            entity.setVacinacao(Vacinacao.builder()
                    .vacinaCovid(dto.getVacinacao().getVacinaCovid())
                    .primeiraDose(dto.getVacinacao().getPrimeiraDose())
                    .segundaDose(dto.getVacinacao().getSegundaDose())
                    .terceiraDose(dto.getVacinacao().getTerceiraDose())
                    .vacinaInfluenza(dto.getVacinacao().getVacinaInfluenza())
                    .build());
        }

        return entity;
    }

    public void updateEntity(Paciente entity, PacienteDTO dto) {
        if (dto.getNome() != null) entity.setNome(dto.getNome());
        if (dto.getCpf() != null) entity.setCpf(dto.getCpf());
        if (dto.getCns() != null) entity.setCns(dto.getCns());
        if (dto.getDataNascimento() != null) entity.setDataNascimento(dto.getDataNascimento());
        if (dto.getSexo() != null) entity.setSexo(dto.getSexo());
        if (dto.getGestante() != null) entity.setGestante(dto.getGestante());
        if (dto.getPuerpera() != null) entity.setPuerpera(dto.getPuerpera());
        if (dto.getRaca() != null) entity.setRaca(dto.getRaca());
        if (dto.getEstadoCivil() != null) entity.setEstadoCivil(dto.getEstadoCivil());
        if (dto.getEscolaridade() != null) entity.setEscolaridade(dto.getEscolaridade());
        if (dto.getProfissao() != null) entity.setProfissao(dto.getProfissao());

        if (dto.getEndereco() != null) {
            if (entity.getEndereco() == null) entity.setEndereco(new Endereco());
            entity.getEndereco().setCep(dto.getEndereco().getCep());
            entity.getEndereco().setLogradouro(dto.getEndereco().getLogradouro());
            entity.getEndereco().setNumero(dto.getEndereco().getNumero());
            entity.getEndereco().setComplemento(dto.getEndereco().getComplemento());
            entity.getEndereco().setBairro(dto.getEndereco().getBairro());
            entity.getEndereco().setMunicipio(dto.getEndereco().getMunicipio());
            entity.getEndereco().setZona(dto.getEndereco().getZona());
            entity.getEndereco().setUf(dto.getEndereco().getUf());
        }

        if (dto.getContato() != null) {
            if (entity.getContato() == null) entity.setContato(new Contato());
            entity.getContato().setTelefone(dto.getContato().getTelefone());
            entity.getContato().setCelular(dto.getContato().getCelular());
            entity.getContato().setEmail(dto.getContato().getEmail());
            entity.getContato().setContatoEmergencia(dto.getContato().getContatoEmergencia());
            entity.getContato().setTelefoneEmergencia(dto.getContato().getTelefoneEmergencia());
        }

        if (dto.getComorbidade() != null) {
            if (entity.getComorbidade() == null) entity.setComorbidade(new Comorbidade());
            entity.getComorbidade().setFatorRisco(dto.getComorbidade().getFatorRisco());
            entity.getComorbidade().setDiabetes(dto.getComorbidade().getDiabetes());
            entity.getComorbidade().setCardiopatia(dto.getComorbidade().getCardiopatia());
            entity.getComorbidade().setAsma(dto.getComorbidade().getAsma());
            entity.getComorbidade().setObesidade(dto.getComorbidade().getObesidade());
            entity.getComorbidade().setRenal(dto.getComorbidade().getRenal());
            entity.getComorbidade().setHepatica(dto.getComorbidade().getHepatica());
            entity.getComorbidade().setPneumopatia(dto.getComorbidade().getPneumopatia());
            entity.getComorbidade().setHematologica(dto.getComorbidade().getHematologica());
            entity.getComorbidade().setNeurologica(dto.getComorbidade().getNeurologica());
            entity.getComorbidade().setImunodepressao(dto.getComorbidade().getImunodepressao());
            entity.getComorbidade().setSindromeDown(dto.getComorbidade().getSindromeDown());
        }

        if (dto.getVacinacao() != null) {
            if (entity.getVacinacao() == null) entity.setVacinacao(new Vacinacao());
            entity.getVacinacao().setVacinaCovid(dto.getVacinacao().getVacinaCovid());
            entity.getVacinacao().setPrimeiraDose(dto.getVacinacao().getPrimeiraDose());
            entity.getVacinacao().setSegundaDose(dto.getVacinacao().getSegundaDose());
            entity.getVacinacao().setTerceiraDose(dto.getVacinacao().getTerceiraDose());
            entity.getVacinacao().setVacinaInfluenza(dto.getVacinacao().getVacinaInfluenza());
        }
    }
}

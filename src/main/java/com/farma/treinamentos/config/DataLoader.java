package com.farma.treinamentos.config;

import com.farma.treinamentos.model.Cargo;
import com.farma.treinamentos.model.Departamento;
import com.farma.treinamentos.model.Utilizador;
import com.farma.treinamentos.model.enums.NivelAcesso;
import com.farma.treinamentos.model.enums.StatusUtilizador;
import com.farma.treinamentos.repository.CargoRepository;
import com.farma.treinamentos.repository.DepartamentoRepository;
import com.farma.treinamentos.repository.UtilizadorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final UtilizadorRepository utilizadorRepository;
    private final DepartamentoRepository departamentoRepository;
    private final CargoRepository cargoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UtilizadorRepository utilizadorRepository, 
                      DepartamentoRepository departamentoRepository,
                      CargoRepository cargoRepository,
                      PasswordEncoder passwordEncoder) {
        this.utilizadorRepository = utilizadorRepository;
        this.departamentoRepository = departamentoRepository;
        this.cargoRepository = cargoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Verificando inicialização do usuário admin...");
        
        // Create default department if not exists
        Departamento deptAdmin = departamentoRepository.findByNome("Administração")
                .orElseGet(() -> {
                    Departamento dept = new Departamento();
                    dept.setNome("Administração");
                    dept.setDescricao("Departamento Administrativo");
                    return departamentoRepository.save(dept);
                });

        // Create default cargo if not exists
        Cargo cargoAdmin = cargoRepository.findByNome("Administrador")
                .orElseGet(() -> {
                    Cargo cargo = new Cargo();
                    cargo.setNome("Administrador");
                    cargo.setDescricao("Cargo de Administrador");
                    cargo.setDepartamento(deptAdmin);
                    return cargoRepository.save(cargo);
                });

        utilizadorRepository.findByEmail("admin@farma.com").ifPresentOrElse(
            admin -> {
                // Se já existe, forçamos o reset da senha para garantir que admin123 funcione
                admin.setSenhaHash(passwordEncoder.encode("admin123"));
                admin.setStatus(StatusUtilizador.ATIVO);
                admin.setNivelAcesso(NivelAcesso.ADMINISTRADOR);
                admin.setDepartamento(deptAdmin);
                admin.setCargo(cargoAdmin);
                utilizadorRepository.save(admin);
                System.out.println("Usuário Admin atualizado com sucesso: admin@farma.com / admin123");
            },
            () -> {
                Utilizador admin = new Utilizador();
                admin.setMatricula("ADMIN");
                admin.setNomeCompleto("Administrador do Sistema");
                admin.setEmail("admin@farma.com");
                admin.setSenhaHash(passwordEncoder.encode("admin123"));
                admin.setStatus(StatusUtilizador.ATIVO);
                admin.setNivelAcesso(NivelAcesso.ADMINISTRADOR);
                admin.setDepartamento(deptAdmin);
                admin.setCargo(cargoAdmin);
                
                utilizadorRepository.save(admin);
                System.out.println("Usuário Admin criado com sucesso: admin@farma.com / admin123");
            }
        );
    }
}

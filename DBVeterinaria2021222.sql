/*
	Programador: Alejandro Arturo Echeverría Garrido
	Código Técnico: IN5AM
	Carné: 2021222
	Fecha de Creación: 08/04/2025
    Fecha de modificación: 09/04/2025
							08/05/2025
                            15/05/2025
                            17/05/2025
                            18/05/2025
                            22/05/2025
                            23/05/2025
                            10/06/2025
                            11/06/2025
*/

-- ALTER USER 'quintom'@'%' identified WITH mysql_native_password by 'admin';
drop database if exists DBVeterinaria2021222;
create database if not exists DBVeterinaria2021222;
use DBVeterinaria2021222;

Create table Clientes(
	codigoCliente int not null auto_increment,
    nombreCliente varchar(75) not null,
    apellidoCliente varchar(75) not null,
    telefonoCliente varchar(8) not null,
    direccionCliente varchar(200) not null,
    correoCliente varchar(80),
    fechaRegistro date not null,
    primary key PK_codigoCliente (codigoCliente)
);

Create table Veterinarios(
	codigoVeterinario int not null auto_increment,
    nombreVeterinario varchar(75) not null,
    apellidoVeterinario varchar(75) not null,
    especialidad varchar(100) not null,
    telefonoVeterinario varchar(8) not null,
    correoVeterinario varchar(80),
    fechaIngreso date not null,
    estado ENUM('Activo','Inactivo'),
    primary key PK_codigoVeterinario (codigoVeterinario)
);

Create table Proveedores(
	codigoProveedor int not null auto_increment,
    nombreProveedor varchar(150) not null,
    direccionProveedor varchar(255) not null,
    telefonoProveedor varchar(8) not null,
    correoProveedor varchar(100) not null,
    primary key PK_codigoProveedor (codigoProveedor)
);

Create table Vacunas(
	codigoVacuna int not null auto_increment,
    nombreVacuna varchar(100) not null,
    descripcion varchar(255) not null,
    dosis varchar(100),
    frecuenciaMeses int,
    primary key PK_codigoVacuna (codigoVacuna)
);

Create table Empleados(
	codigoEmpleado int not null auto_increment,
    nombreEmpleado varchar(75) not null,
    apellidoEmpleado varchar(75) not null,
    cargo varchar(50) not null,
    telefonoEmpleado varchar(8) not null,
    correoEmpleado varchar(100) not null,
    primary key PK_codigoEmpleado (codigoEmpleado)
);

Create table Mascotas(
	codigoMascota int not null auto_increment,
    nombreMascota varchar(50) not null,
    especie varchar(50) not null,
    raza varchar(50) not null,
    sexo ENUM('Macho','Hembra'),
    fechaNacimiento date not null,
    color varchar(50),
    pesoActualKg decimal(5,2),
    codigoCliente int not null,
    primary key PK_codigoMascota (codigoMascota),
    constraint FK_Mascotas_Clientes foreign key (codigoCliente) references Clientes(codigoCliente) on delete cascade
);

Create table Consultas(
	codigoConsulta int not null auto_increment,
    fechaConsulta datetime not null,
    motivo varchar(255) not null,
    diagnostico varchar(255) not null,
    observaciones varchar(255) not null,
    codigoMascota int not null,
    codigoVeterinario int not null,
    primary key PK_codigoConsulta (codigoConsulta),
    constraint FK_Consultas_Mascotas foreign key (codigoMascota) references Mascotas(codigoMascota) on delete cascade,
    constraint FK_Consultas_Veterinarios foreign key (codigoVeterinario) references Veterinarios(codigoVeterinario) on delete cascade
);

Create table Tratamientos(
	codigoTratamiento int not null auto_increment,
    descripcion varchar(255) not null,
    fechaInicio date not null,
    fechaFin date not null,
    medicamentosIndicados varchar(250),
    codigoConsulta int not null,
    primary key PK_codigoTratamiento (codigoTratamiento),
    constraint FK_Tratamientos_Consultas foreign key (codigoConsulta) references Consultas(codigoConsulta) on delete cascade
);

Create table Citas(
	codigoCita int not null auto_increment,
    fechaCita date not null,
    horaCita time not null,
    motivo varchar(250) not null,
    estado ENUM('Pendiente','Completada','Cancelada'),
    codigoMascota int not null,
    codigoVeterinario int not null,
    primary key PK_codigoCita (codigoCita),
    constraint FK_Citas_Mascotas foreign key (codigoMascota) references Mascotas(codigoMascota) on delete cascade,
    constraint FK_Citas_Veterinarios foreign key (codigoVeterinario) references Veterinarios(codigoVeterinario) on delete cascade
);

Create table Vacunaciones(
	codigoVacunacion int not null auto_increment,
    fechaAplicacion date not null,
    observaciones varchar(250) not null,
    codigoMascota int not null,
    codigoVacuna int not null,
    codigoVeterinario int not null,
    primary key PK_codigoVacunacion (codigoVacunacion),
    constraint FK_Vacunaciones_Mascotas foreign key (codigoMascota) references Mascotas(codigoMascota) on delete cascade,
    constraint FK_Vacunaciones_Vacunas foreign key (codigoVacuna) references Vacunas(codigoVacuna) on delete cascade,
    constraint FK_Vacunaciones_Veterinarios foreign key (codigoVeterinario) references Veterinarios(codigoVeterinario) on delete cascade
);

Create table Medicamentos(
	codigoMedicamento int not null auto_increment,
    nombreMedicamento varchar(100) not null,
    descripcion varchar(250) not null,
    stock int not null,
    precioUnitario decimal(10,2) not null,
    fechaVencimiento date,
    codigoProveedor int not null,
    primary key PK_codigoMedicamento (codigoMedicamento),
    constraint FK_Medicamentos_Proveedores foreign key (codigoProveedor) references Proveedores(codigoProveedor) on delete cascade
);

Create table Recetas(
	codigoReceta int not null auto_increment,
    dosis varchar(100) not null,
    frecuencia varchar(100) not null,
    duracionDosis int not null,
    indicaciones varchar(250),
    codigoConsulta int not null,
    codigoMedicamento int not null,
    primary key PK_codigoReceta (codigoReceta),
    constraint FK_Recetas_Consultas foreign key (codigoConsulta) references Consultas(codigoConsulta) on delete cascade,
    constraint FK_Recetas_Medicamentos foreign key (codigoMedicamento) references Medicamentos(codigoMedicamento) on delete cascade
);

Create table Facturas(
	codigoFactura int not null auto_increment,
    fechaEmision date not null,
    total decimal(10,2) default 0.00,
    metodoPago ENUM ('Efectivo','Tarjeta','Transferencia'),
    codigoCliente int not null,
    codigoEmpleado int not null,
    primary key PK_codigoFactura (codigoFactura),
    constraint FK_Facturas_Clientes foreign key (codigoCliente) references Clientes(codigoCliente) on delete cascade,
    constraint FK_Facturas_Empleados foreign key (codigoEmpleado) references Empleados(codigoEmpleado) on delete cascade
);

Create table Compras(
	codigoCompra int not null auto_increment,
    fechaCompra date not null,
    total decimal(10,2) not null,
    detalle varchar(255) not null,
    codigoProveedor int not null,
    primary key PK_codigoCompra (codigoCompra),
    constraint FK_Compras_Proveedores foreign key (codigoProveedor) references Proveedores (codigoProveedor) on delete cascade
);

-- ------------- PROCEDIMIENTOS ALMACENADOS --------------------
-- TODOS LOS PROCEDIMIENTOS JUNTOS POR ENTIDAD -----------------


-- ----------------- ENTIDAD CLIENTES --------------------------
-- AGREGAR CLIENTE
Delimiter //
	create procedure sp_AgregarCliente (
		in nombreCliente varchar(75),
        in apellidoCliente varchar(75),
        in telefonoCliente varchar(8),
        in direccionCliente varchar(200),
        in correoCliente varchar(80),
        in fechaRegistro date)
        Begin
			insert into Clientes (nombreCliente, apellidoCliente, telefonoCliente, direccionCliente, correoCliente, fechaRegistro)
				values (nombreCliente, apellidoCliente, telefonoCliente, direccionCliente, correoCliente, fechaRegistro);
        End//
Delimiter ;
call sp_AgregarCliente('Louis Hugo','Xicara Camey','12451245','6 ta Avenida 13-54 zona 7 Colonia Landivar','lxicara-2021175@gmail.com','2025-04-08');

-- LISTAR CLIENTES
Delimiter //
	create procedure sp_ListarClientes()
		Begin
			Select
				C.codigoCliente, 
				C.nombreCliente, 
				C.apellidoCliente, 
				C.telefonoCliente, 
				C.direccionCliente, 
				C.correoCliente, 
				C.fechaRegistro
			from Clientes C;
        End//
Delimiter ;
call sp_ListarClientes();

-- ELIMINAR CLIENTE
Delimiter //
	create procedure sp_EliminarCliente(
		in codCliente int)
        Begin
			delete from Clientes C
				where C.codigoCliente = codCliente;
        End//
Delimiter ;
-- call sp_EliminarCliente(2);

-- BUSCAR CLIENTE
Delimiter //
	create procedure sp_BuscarCliente (
		in codCliente int)
        Begin
			select
				C.codigoCliente, 
				C.nombreCliente, 
				C.apellidoCliente, 
				C.telefonoCliente, 
				C.direccionCliente, 
				C.correoCliente, 
				C.fechaRegistro
			from Clientes C
				where C.codigoCliente = codCliente;
        End//
Delimiter ;
-- call sp_BuscarCliente(1);

-- EDITAR CLIENTE
Delimiter //
	create procedure sp_EditarCliente(
		in codCliente int,
        in nomCliente varchar(75),
        in apeCliente varchar(75),
        in telCliente varchar(8),
        in direCliente varchar(200),
        in corrCliente varchar(80),
        in feRegistro date)
        Begin
			update Clientes C
				set
					C.nombreCliente = nomCliente, 
					C.apellidoCliente = apeCliente, 
					C.telefonoCliente = telCliente, 
					C.direccionCliente = direCliente, 
					C.correoCliente = corrCliente, 
					C.fechaRegistro = feRegistro
                where C.codigoCliente = codCliente;
        End//
Delimiter ;
-- call sp_EditarCliente(1,'Mauricio Nelson','Tax Xiloj','45454545','30 Av. 5-19 Zona 4, Mixco','mtax-2021465@gmail.com','2025-04-08');

-- ----------------- ENTIDAD VETERINARIOS --------------------------
-- AGREGAR VETERINARIO
Delimiter //
	create procedure sp_AgregarVeterinario (
		in nombreVeterinario varchar(75),
        in apellidoVeterinario varchar(75),
        in especialidad varchar(100),
        in telefonoVeterinario varchar(8),
        in correoVeterinario varchar(80),
        in fechaIngreso date,
        in estado varchar(15))
        Begin
			insert into Veterinarios (nombreVeterinario, apellidoVeterinario, especialidad, telefonoVeterinario, correoVeterinario, fechaIngreso,estado)
				values (nombreVeterinario, apellidoVeterinario, especialidad, telefonoVeterinario, correoVeterinario, fechaIngreso,estado);
        End//
Delimiter ;
call sp_AgregarVeterinario('Nelson Brando','Xiloj Flores','Caninos','46464646','nxiloj-2020222@gmail.com','2025-03-08','Activo');

-- LISTAR VETERINARIOS
Delimiter //
	create procedure sp_ListarVeterinarios()
		Begin
			Select
				V.codigoVeterinario, 
                V.nombreVeterinario, 
                V.apellidoVeterinario, 
                V.especialidad, 
                V.telefonoVeterinario, 
                V.correoVeterinario, 
                V.fechaIngreso, 
                V.estado
			from Veterinarios V;
        End//
Delimiter ;
call sp_ListarVeterinarios();

-- ELIMINAR VETERINARIO
Delimiter //
	create procedure sp_EliminarVeterinario(
		in codVeterinario int)
        Begin
			delete from Veterinarios V
				where V.codigoVeterinario = codVeterinario;
        End//
Delimiter ;
-- call sp_EliminarVeterinario(2);

-- BUSCAR VETERINARIO
Delimiter //
	create procedure sp_BuscarVeterinario (
		in codVeterinario int)
        Begin
			select
				V.codigoVeterinario, 
                V.nombreVeterinario, 
                V.apellidoVeterinario, 
                V.especialidad, 
                V.telefonoVeterinario, 
                V.correoVeterinario, 
                V.fechaIngreso, 
                V.estado
			from Veterinarios V
				where V.codigoVeterinario = codVeterinario;
        End//
Delimiter ;
-- call sp_BuscarVeterinario(1);

-- EDITAR VETERINARIO
Delimiter //
	create procedure sp_EditarVeterinario(
		in codVeterinario int,
        in nomVeterinario varchar(75),
        in apeVeterinario varchar(75),
        in especial varchar(100),
        in telVeterinario varchar(8),
        in corrVeterinario varchar(80),
        in feIngreso date,
        in esta varchar(15))
        Begin
			update Veterinarios V
				set
					V.nombreVeterinario = nomVeterinario, 
					V.apellidoVeterinario = apeVeterinario, 
					V.especialidad = especial, 
					V.telefonoVeterinario = telVeterinario, 
					V.correoVeterinario = corrVeterinario, 
					V.fechaIngreso = feIngreso,
					V.estado = esta
                where V.codigoVeterinario = codVeterinario;
        End//
Delimiter ;
-- call sp_EditarVeterinario(1,'Pedro Brando','Xiloj Flores','Gatos','48464646','pxiloj-2020221@gmail.com','2025-03-08','Inactivo');


-- ----------------- ENTIDAD PROVEEDORES --------------------------
-- AGREGAR PROVEEDOR
Delimiter //
	create procedure sp_AgregarProveedor (
		in nombreProveedor varchar(150),
        in direccionProveedor varchar(255),
        in telefonoProveedor varchar(8),
        in correoProveedor varchar(100))
        Begin
			insert into Proveedores (nombreProveedor, direccionProveedor, telefonoProveedor, correoProveedor)
				values (nombreProveedor, direccionProveedor, telefonoProveedor, correoProveedor);
        End//
Delimiter ;
call sp_AgregarProveedor('AmigosCaninos','30 calle 06-15 zona 11 Colonia Granai & Towson I','78787878','acaninos@gmail.com');

-- LISTAR PROVEEDORES
Delimiter //
	create procedure sp_ListarProveedores()
		Begin
			Select
				P.codigoProveedor, 
                P.nombreProveedor, 
                P.direccionProveedor, 
                P.telefonoProveedor, 
                P.correoProveedor
			from Proveedores P;
        End//
Delimiter ;
call sp_ListarProveedores();

-- ELIMINAR PROVEEDOR
Delimiter //
	create procedure sp_EliminarProveedor(
		in codProveedor int)
        Begin
			delete from Proveedores P
				where P.codigoProveedor = codProveedor;
        End//
Delimiter ;
-- call sp_EliminarProveedor(3);

-- BUSCAR PROVEEDOR
Delimiter //
	create procedure sp_BuscarProveedor (
		in codProveedor int)
        Begin
			select
				P.codigoProveedor, 
                P.nombreProveedor, 
                P.direccionProveedor, 
                P.telefonoProveedor, 
                P.correoProveedor
			from Proveedores P
				where P.codigoProveedor = codProveedor;
        End//
Delimiter ;
-- call sp_BuscarProveedor(2);

-- EDITAR PROVEEDOR
Delimiter //
	create procedure sp_EditarProveedor(
		in codProveedor int,
		in nomProveedor varchar(150),
        in direProveedor varchar(255),
        in telProveedor varchar(8),
        in corrProveedor varchar(100))
        Begin
			update Proveedores P
				set
					P.nombreProveedor = nomProveedor, 
					P.direccionProveedor = direProveedor,
					P.telefonoProveedor = telProveedor, 
					P.correoProveedor = corrProveedor
                where P.codigoProveedor = codProveedor;
        End//
Delimiter ;
-- call sp_EditarProveedor(2,'AmigosC','31 calle 06-15 zona 11 Colonia Granai & Towson I','78887878','acani@gmail.com');


-- ----------------- ENTIDAD VACUNAS --------------------------
-- AGREGAR VACUNA
Delimiter //
	create procedure sp_AgregarVacuna (
		in nombreVacuna varchar(100),
        in descripcion varchar(255),
        in dosis varchar(100),
        in frecuenciaMeses int)
        Begin
			insert into Vacunas (nombreVacuna, descripcion, dosis, frecuenciaMeses)
				values (nombreVacuna, descripcion, dosis, frecuenciaMeses);
        End//
Delimiter ;
call sp_AgregarVacuna('Fayzeer','Vacuna para la rabia.','100 ml',2);

-- LISTAR VACUNAS
Delimiter //
	create procedure sp_ListarVacunas()
		Begin
			Select
				Va.codigoVacuna, 
                Va.nombreVacuna, 
                Va.descripcion, 
                Va.dosis, 
                Va.frecuenciaMeses
			from Vacunas Va;
        End//
Delimiter ;
call sp_ListarVacunas();

-- ELIMINAR VACUNA
Delimiter //
	create procedure sp_EliminarVacuna(
		in codVacuna int)
        Begin
			delete from Vacunas Va
				where Va.codigoVacuna = codVacuna;
        End//
Delimiter ;
-- call sp_EliminarVacuna(3);

-- BUSCAR VACUNA
Delimiter //
	create procedure sp_BuscarVacuna (
		in codVacuna int)
        Begin
			select
				Va.codigoVacuna, 
                Va.nombreVacuna, 
                Va.descripcion, 
                Va.dosis, 
                Va.frecuenciaMeses
			from Vacunas Va
				where Va.codigoVacuna = codVacuna;
        End//
Delimiter ;
-- call sp_BuscarVacuna(2);

-- EDITAR VACUNA
Delimiter //
	create procedure sp_EditarVacuna(
		in codVacuna int,
        in nomVacuna varchar(100),
        in desVa varchar(255),
        in dos varchar(100),
        in freMeses int)
        Begin
			update Vacunas Va
				set
					Va.nombreVacuna = nomVacuna, 
					Va.descripcion = desVa, 
					Va.dosis = dos, 
					Va.frecuenciaMeses = freMeses
                where Va.codigoVacuna = codVacuna;
        End//
Delimiter ;
-- call sp_EditarVacuna(2,'Fayzr','Vacuna para el pelo.','23 ml',4);


-- ----------------- ENTIDAD EMPLEADOS --------------------------
-- AGREGAR EMPLEADO
Delimiter //
	create procedure sp_AgregarEmpleado (
		in nombreEmpleado varchar(75),
        in apellidoEmpleado varchar(75),
        in cargo varchar(50),
        in telefonoEmpleado varchar(8),
        in correoEmpleado varchar(100))
        Begin
			insert into Empleados (nombreEmpleado, apellidoEmpleado, cargo, telefonoEmpleado, correoEmpleado)
				values (nombreEmpleado, apellidoEmpleado, cargo, telefonoEmpleado, correoEmpleado);
        End//
Delimiter ;
call sp_AgregarEmpleado('Oswald Mauricio','Cua Subuyul','Limpieza','89898989','ocua@gmail.com');

-- LISTAR EMPLEADOS
Delimiter //
	create procedure sp_ListarEmpleados()
		Begin
			Select
				E.codigoEmpleado, 
                E.nombreEmpleado, 
                E.apellidoEmpleado, 
                E.cargo,
                E.telefonoEmpleado,
                E.correoEmpleado
			from Empleados E;
        End//
Delimiter ;
call sp_ListarEmpleados();


-- ELIMINAR EMPLEADO
Delimiter //
	create procedure sp_EliminarEmpleado(
		in codEmpleado int)
        Begin
			delete from Empleados E
				where E.codigoEmpleado = codEmpleado;
        End//
Delimiter ;
-- call sp_EliminarEmpleado(3);

-- BUSCAR EMPLEADO
Delimiter //
	create procedure sp_BuscarEmpleado (
		in codEmpleado int)
        Begin
			select
				E.codigoEmpleado, 
                E.nombreEmpleado, 
                E.apellidoEmpleado, 
                E.cargo,
                E.telefonoEmpleado,
                E.correoEmpleado
			from Empleados E
				where E.codigoEmpleado = codEmpleado;
        End//
Delimiter ;
-- call sp_BuscarEmpleado(2);


-- EDITAR EMPLEADO
Delimiter //
	create procedure sp_EditarEmpleado(
		in codEmpleado int,
        in nomEmpleado varchar(75),
        in apeEmpleado varchar(75),
        in carEmpleado varchar(50),
        in telEmpleado varchar(8),
        in corrEmpleado varchar(100))
        Begin
			update Empleados E
				set
					E.nombreEmpleado = nomEmpleado, 
					E.apellidoEmpleado = apeEmpleado, 
					E.cargo = carEmpleado, 
					E.telefonoEmpleado = telEmpleado, 
					E.correoEmpleado = corrEmpleado
                where E.codigoEmpleado = codEmpleado;
        End//
Delimiter ;
-- call sp_EditarEmpleado(2,'Oswaldo Fabricio','Tun Catalan','Limpieza','89208989','otun@gmail.com');


-- ----------------- ENTIDAD MASCOTAS --------------------------
-- AGREGAR MASCOTA
Delimiter //
	create procedure sp_AgregarMascota (
		in nombreMascota varchar(50),
        in especie varchar(50),
        in raza varchar(50),
        in sexo varchar(15),
        in fechaNacimiento date,
        in color varchar(50),
        in pesoActualKg decimal(5,2),
        in codigoCliente int)
        Begin
			insert into Mascotas (nombreMascota, especie, raza, sexo, fechaNacimiento, color, pesoActualKg, codigoCliente)
				values (nombreMascota, especie, raza, sexo, fechaNacimiento, color, pesoActualKg, codigoCliente);
        End//
Delimiter ;
call sp_AgregarMascota('Luna','Perro','Dachshund','Hembra','2021-01-30','Negro','20.5',1);

-- LISTAR MASCOTAS
Delimiter //
	create procedure sp_ListarMascotas()
		Begin
			Select
				M.codigoMascota, 
                M.nombreMascota, 
                M.especie, 
                M.raza, 
                M.sexo, 
                M.fechaNacimiento, 
                M.color, 
                M.pesoActualKg,
                M.codigoCliente
			from Mascotas M;
        End//
Delimiter ;
call sp_ListarMascotas();

-- ELIMINAR MASCOTA
Delimiter //
	create procedure sp_EliminarMascota(
		in codMascota int)
        Begin
			delete from Mascotas M
				where M.codigoMascota = codMascota;
        End//
Delimiter ;
-- call sp_EliminarMascota(3);

-- BUSCAR MASCOTA
Delimiter //
	create procedure sp_BuscarMascota (
		in codMascota int)
        Begin
			select
				M.codigoMascota, 
                M.nombreMascota, 
                M.especie, 
                M.raza, 
                M.sexo, 
                M.fechaNacimiento, 
                M.color, 
                M.pesoActualKg,
                M.codigoCliente
			from Mascotas M
				where M.codigoMascota = codMascota;
        End//
Delimiter ;
-- call sp_BuscarMascota(2);

-- EDITAR MASCOTA
Delimiter //
	create procedure sp_EditarMascota(
		in codMascota int,
        in nomMascota varchar(50),
        in esp varchar(50),
        in raz varchar(50),
        in se varchar(15),
        in feNacimien date,
        in col varchar(50),
        in peActualKg decimal(5,2))
        Begin
			update Mascotas M
				set
					M.nombreMascota = nomMascota, 
					M.especie = esp, 
					M.raza = raz, 
					M.sexo = se, 
					M.fechaNacimiento = feNacimien, 
					M.color = col, 
					M.pesoActualKg = peActualKg
                where M.codigoMascota = codMascota;
        End//
Delimiter ;
call sp_EditarMascota(2,'Max','Perro','Chihuahua','Macho','2021-03-14','Negro','18.5');



-- ----------------- ENTIDAD CONSULTAS --------------------------
-- AGREGAR CONSULTA
Delimiter //
	create procedure sp_AgregarConsulta (
		in fechaConsulta datetime,
        in motivo varchar(255),
        in diagnostico varchar(255),
        in observaciones varchar(255),
        in codigoMascota int,
        in codigoVeterinario int)
        Begin
			insert into Consultas (fechaConsulta, motivo, diagnostico, observaciones, codigoMascota, codigoVeterinario)
				values (fechaConsulta, motivo, diagnostico, observaciones, codigoMascota, codigoVeterinario);
        End//
Delimiter ;
call sp_AgregarConsulta('2025-04-08 20:32:02','Diarrea','Diarrea causada por mala alimentación','Ojos rojos',1,1);

-- LISTAR CONSULTAS
Delimiter //
	create procedure sp_ListarConsultas()
		Begin
			Select
				Co.codigoConsulta, 
                DATE_FORMAT(Co.fechaConsulta, '%Y-%m-%d %H:%i:%s') AS fechaConsulta,
                Co.motivo, 
                Co.diagnostico, 
                Co.observaciones, 
                Co.codigoMascota, 
                Co.codigoVeterinario
			from Consultas Co;
        End//
Delimiter ;
call sp_ListarConsultas();


-- ELIMINAR CONSULTA
Delimiter //
	create procedure sp_EliminarConsulta(
		in codConsulta int)
        Begin
			delete from Consultas Co
				where Co.codigoConsulta = codConsulta;
        End//
Delimiter ;
-- call sp_EliminarConsulta(3);

-- BUSCAR CONSULTA
Delimiter //
	create procedure sp_BuscarConsulta (
		in codConsulta int)
        Begin
			select
				Co.codigoConsulta, 
                Co.fechaConsulta, 
                Co.motivo, 
                Co.diagnostico, 
                Co.observaciones, 
                Co.codigoMascota, 
                Co.codigoVeterinario
			from Consultas Co
				where Co.codigoConsulta = codConsulta;
        End//
Delimiter ;
-- call sp_BuscarConsulta(2);

-- EDITAR CONSULTA
Delimiter //
	create procedure sp_EditarConsulta(
		in codConsulta int,
        in fecConsulta datetime,
        in motivoCon varchar(255),
        in diagno varchar(255),
        in observar varchar(255))
        Begin
			update Consultas Co
				set
					Co.fechaConsulta = fecConsulta, 
					Co.motivo = motivoCon, 
					Co.diagnostico = diagno, 
					Co.observaciones = observar
                where Co.codigoConsulta = codConsulta;
        End//
Delimiter ;
-- call sp_EditarConsulta(2,'2025-04-08 20:32:02','Diarrea','Diarrea causada por hongos','Ojos verdes');



-- ----------------- ENTIDAD TRATAMIENTOS --------------------------
-- AGREGAR TRATAMIENTO
Delimiter //
	create procedure sp_AgregarTratamiento (
		in descripcion varchar(255),
        in fechaInicio date,
        in fechaFin date,
        in medicamentosIndicados varchar(250),
        in codigoConsulta int)
        Begin
			insert into Tratamientos (descripcion, fechaInicio, fechaFin, medicamentosIndicados, codigoConsulta)
				values (descripcion, fechaInicio, fechaFin, medicamentosIndicados, codigoConsulta);
        End//
Delimiter ;
call sp_AgregarTratamiento('Ingerir polvo para diarrea, todos los días.','2025-04-08','2025-04-16','MXDiarreax',1);

-- LISTAR TRATAMIENTOS
Delimiter //
	create procedure sp_ListarTratamientos()
		Begin
			Select
				T.codigoTratamiento, 
                T.descripcion, 
                T.fechaInicio, 
                T.fechaFin, 
                T.medicamentosIndicados, 
                T.codigoConsulta
			from Tratamientos T;
        End//
Delimiter ;
call sp_ListarTratamientos();

-- ELIMINAR TRATAMIENTO
Delimiter //
	create procedure sp_EliminarTratamiento(
		in codTratamiento int)
        Begin
			delete from Tratamientos T
				where T.codigoTratamiento = codTratamiento;
        End//
Delimiter ;

-- call sp_EliminarTratamiento(3);

-- BUSCAR TRATAMIENTO
Delimiter //
	create procedure sp_BuscarTratamiento (
		in codTratamiento int)
        Begin
			select
				T.codigoTratamiento, 
                T.descripcion, 
                T.fechaInicio, 
                T.fechaFin, 
                T.medicamentosIndicados, 
                T.codigoConsulta
			from Tratamientos T
				where T.codigoTratamiento = codTratamiento;
        End//
Delimiter ;
-- call sp_BuscarTratamiento(2);

-- EDITAR TRATAMIENTO
Delimiter //
	create procedure sp_EditarTratamiento(
		in codTratamiento int,
        in desTrata varchar(255),
        in fecInicio date,
        in fecFin date,
        in mediIndicados varchar(250))
        Begin
			update Tratamientos T
				set
					T.descripcion = desTrata, 
					T.fechaInicio = fecInicio, 
					T.fechaFin = fecFin, 
					T.medicamentosIndicados = mediIndicados
                where T.codigoTratamiento = codTratamiento;
        End//
Delimiter ;
-- call sp_EditarTratamiento(2,'Tomar polvo para diarria, todos los días.','2025-04-08','2025-04-16','Diarreax');


-- ----------------- ENTIDAD CITAS --------------------------
-- AGREGAR CITA
Delimiter //
	create procedure sp_AgregarCita (
		in fechaCita date,
        in horaCita time,
        in motivo varchar(250),
        in estado varchar(15),
        in codigoMascota int,
        in codigoVeterinario int)
        Begin
			insert into Citas (fechaCita, horaCita, motivo, estado, codigoMascota, codigoVeterinario)
				values (fechaCita, horaCita, motivo, estado, codigoMascota, codigoVeterinario);
        End//
Delimiter ;
call sp_AgregarCita('2025-04-09','07:15:45','Mascota con color rojo en los ojos.','Completada',1,1);

-- LISTAR CITAS
Delimiter //
	create procedure sp_ListarCitas()
		Begin
			Select
				Ci.codigoCita, 
                Ci.fechaCita, 
                Ci.horaCita, 
                Ci.motivo, 
                Ci.estado, 
                Ci.codigoMascota, 
                Ci.codigoVeterinario
			from Citas Ci;
        End//
Delimiter ;
call sp_ListarCitas();

-- ELIMINAR CITA
Delimiter //
	create procedure sp_EliminarCita(
		in codCita int)
        Begin
			delete from Citas Ci
				where Ci.codigoCita = codCita;
        End//
Delimiter ;
-- call sp_EliminarCita(3);

-- BUSCAR CITA
Delimiter //
	create procedure sp_BuscarCita (
		in codCita int)
        Begin
			select
				Ci.codigoCita, 
                Ci.fechaCita, 
                Ci.horaCita, 
                Ci.motivo, 
                Ci.estado, 
                Ci.codigoMascota, 
                Ci.codigoVeterinario
			from Citas Ci
				where Ci.codigoCita = codCita;
        End//
Delimiter ;
-- call sp_BuscarCita(2);

-- EDITAR CITA
Delimiter //
	create procedure sp_EditarCita(
		in codCita int,
        in feCita date,
        in hoCita time,
        in mot varchar(250),
        in estados varchar(15))
        Begin
			update Citas Ci
				set
					Ci.fechaCita = feCita, 
					Ci.horaCita = hoCita, 
					Ci.motivo = mot, 
					Ci.estado = estados
                where Ci.codigoCita = codCita;
        End//
Delimiter ;
-- call sp_EditarCita(2,'2024-04-09','08:13:45','Mascota con pelo endurecido.','Cancelada');


-- ----------------- ENTIDAD VACUNACIONES --------------------------
-- AGREGAR VACUNACION
Delimiter //
	create procedure sp_AgregarVacunaciones (
		in fechaAplicacion date,
        in observaciones varchar(250),
        in codigoMascota int,
        in codigoVacuna int,
        in codigoVeterinario int)
        Begin
			insert into Vacunaciones (fechaAplicacion, observaciones, codigoMascota, codigoVacuna, codigoVeterinario)
				values (fechaAplicacion, observaciones, codigoMascota, codigoVacuna, codigoVeterinario);
        End//
Delimiter ;
call sp_AgregarVacunaciones('2025-05-19','La mascota no tuvo reacciones',1,1,1);

-- LISTAR VACUNACIONES
Delimiter //
	create procedure sp_ListarVacunaciones()
		Begin
			Select
				Vac.codigoVacunacion,
                Vac.fechaAplicacion,
                Vac.observaciones, 
                Vac.codigoMascota, 
                Vac.codigoVacuna, 
                Vac.codigoVeterinario
			from Vacunaciones Vac;
        End//
Delimiter ;
call sp_ListarVacunaciones();


-- ELIMINAR VACUNACION
Delimiter //
	create procedure sp_EliminarVacunacion(
		in codVacunacion int)
        Begin
			delete from Vacunaciones Vac
				where Vac.codigoVacunacion = codVacunacion;
        End//
Delimiter ;
-- call sp_EliminarVacunacion(3);

-- BUSCAR VACUNACION
Delimiter //
	create procedure sp_BuscarVacunaciones (
		in codVacunacion int)
        Begin
			select
				Vac.codigoVacunacion,
                Vac.fechaAplicacion,
                Vac.observaciones, 
                Vac.codigoMascota, 
                Vac.codigoVacuna, 
                Vac.codigoVeterinario
			from Vacunaciones Vac
				where Vac.codigoVacunacion = codVacunacion;
        End//
Delimiter ;
-- call sp_BuscarVacunacion(2);


-- EDITAR VACUNACION
Delimiter //
	create procedure sp_EditarVacunaciones(
		in codVacunacion int,
        in fecAplicaciones date,
        in obsvaciones varchar(250))
        Begin
			update Vacunaciones Vac
				set
					Vac.fechaAplicacion = fecAplicaciones, 
					Vac.observaciones = obsvaciones
                where Vac.codigoVacunacion = codVacunacion;
        End//
Delimiter ;
-- call sp_EditarVacunaciones(2,'2025-06-25','La mascota si tuvo reacciones');


-- ----------------- ENTIDAD MEDICAMENTOS --------------------------
-- AGREGAR MEDICAMENTO
Delimiter //
	create procedure sp_AgregarMedicamento (
		in nombreMedicamento varchar(100),
        in descripcion varchar(250),
        in stock int,
        in precioUnitario decimal(10,2),
        in fechaVencimiento date,
        in codigoProveedor int)
        Begin
			insert into Medicamentos (nombreMedicamento, descripcion, stock, precioUnitario, fechaVencimiento, codigoProveedor)
				values (nombreMedicamento, descripcion, stock, precioUnitario, fechaVencimiento, codigoProveedor);
        End//
Delimiter ;
call sp_AgregarMedicamento('Simparica','Tableta Masticable Antipulgas',23,12.60,'2026-04-09',1);

-- LISTAR MEDICAMENTOS
Delimiter //
	create procedure sp_ListarMedicamentos()
		Begin
			Select
				Me.codigoMedicamento, 
                Me.nombreMedicamento, 
                Me.descripcion, 
                Me.stock, 
                Me.precioUnitario, 
                Me.fechaVencimiento, 
                Me.codigoProveedor
			from Medicamentos Me;
        End//
Delimiter ;
call sp_ListarMedicamentos();

-- ELIMINAR MEDICAMENTO
Delimiter //
	create procedure sp_EliminarMedicamento(
		in codMedicamento int)
        Begin
			delete from Medicamentos Me
				where Me.codigoMedicamento = codMedicamento;
        End//
Delimiter ;
-- call sp_EliminarMedicamento(3);

-- BUSCAR MEDICAMENTO
Delimiter //
	create procedure sp_BuscarMedicamento (
		in codMedicamento int)
        Begin
			select
				Me.codigoMedicamento, 
                Me.nombreMedicamento, 
                Me.descripcion, 
                Me.stock, 
                Me.precioUnitario, 
                Me.fechaVencimiento, 
                Me.codigoProveedor
			from Medicamentos Me
				where Me.codigoMedicamento = codMedicamento;
        End//
Delimiter ;
-- call sp_BuscarMedicamento(2);

-- EDITAR MEDICAMENTO
Delimiter //
	create procedure sp_EditarMedicamento(
		in codMedicamento int,
        in nomMedicamento varchar(100),
        in descripcions varchar(250),
        in stocks int,
        in preciosUnitarios decimal(10,2),
        in fecVencimientos date)
        Begin
			update Medicamentos Me
				set
					Me.nombreMedicamento = nomMedicamento, 
					Me.descripcion = descripcions, 
					Me.stock = stocks, 
					Me.precioUnitario = preciosUnitarios, 
					Me.fechaVencimiento = fecVencimientos
                where Me.codigoMedicamento = codMedicamento;
        End//
Delimiter ;
-- call sp_EditarMedicamento(2,'RicaSimp','Tableta Masticable Antipulgas',30,10.60,'2027-04-09');



-- ----------------- ENTIDAD RECETAS --------------------------
-- AGREGAR RECETA
Delimiter //
	create procedure sp_AgregarReceta (
		in dosis varchar(100),
        in frecuencia varchar(100),
        in duracionDosis int,
        in indicaciones varchar(250),
        in codigoConsulta int,
        in codigoMedicamento int)
        Begin
			insert into Recetas (dosis, frecuencia, duracionDosis, indicaciones, codigoConsulta, codigoMedicamento)
				values (dosis, frecuencia, duracionDosis, indicaciones, codigoConsulta, codigoMedicamento);
        End//
Delimiter ;
call sp_AgregarReceta('5ml','Dos veces al día',5,'Aplicar 5ml a la mascota.',1,1);

-- LISTAR RECETAS
Delimiter //
	create procedure sp_ListarRecetas()
		Begin
			Select
				R.codigoReceta, 
                R.dosis, 
                R.frecuencia, 
                R.duracionDosis, 
                R.indicaciones, 
                R.codigoConsulta, 
                R.codigoMedicamento
			from Recetas R;
        End//
Delimiter ;
call sp_ListarRecetas();

-- ELIMINAR RECETA
Delimiter //
	create procedure sp_EliminarReceta(
		in codReceta int)
        Begin
			delete from Recetas R
				where R.codigoReceta = codReceta;
        End//
Delimiter ;
-- call sp_EliminarReceta(3);

-- BUSCAR RECETA
Delimiter //
	create procedure sp_BuscarReceta (
		in codReceta int)
        Begin
			select
				R.codigoReceta, 
                R.dosis, 
                R.frecuencia, 
                R.duracionDosis, 
                R.indicaciones, 
                R.codigoConsulta, 
                R.codigoMedicamento
			from Recetas R
				where R.codigoReceta = codReceta;
        End//
Delimiter ;
-- call sp_BuscarReceta(2);

-- EDITAR RECETA
Delimiter //
	create procedure sp_EditarReceta(
		in codReceta int,
        in dsis varchar(100),
        in frecuencias varchar(100),
        in duraDosis int,
        in indcacines varchar(250))
        Begin
			update Recetas R
				set
					R.dosis = dsis, 
					R.frecuencia = frecuencias, 
					R.duracionDosis = duraDosis, 
					R.indicaciones = indcacines
                where R.codigoReceta = codReceta;
        End//
Delimiter ;
-- call sp_EditarReceta(2,'10ml','Tres veces al día',7,'Aplicar 10ml a la mascota.');


-- ----------------- ENTIDAD FACTURAS --------------------------
-- AGREGAR FACTURA
Delimiter //
	create procedure sp_AgregarFactura (
		in fechaEmision date,
        in metodoPago varchar(15),
        in codigoCliente int,
        in codigoEmpleado int)
        Begin
			insert into Facturas (fechaEmision, metodoPago, codigoCliente, codigoEmpleado)
				values (fechaEmision, metodoPago, codigoCliente, codigoEmpleado);
        End//
Delimiter ;
call sp_AgregarFactura('2025-04-09','Efectivo',1,1);

-- LISTAR FACTURAS
Delimiter //
	create procedure sp_ListarFacturas()
		Begin
			Select
				F.codigoFactura, 
                F.fechaEmision, 
                F.total, 
                F.metodoPago, 
                F.codigoCliente, 
                F.codigoEmpleado
			from Facturas F;
        End//
Delimiter ;
call sp_ListarFacturas();

-- ELIMINAR FACTURA
Delimiter //
	create procedure sp_EliminarFactura(
		in codFactura int)
        Begin
			delete from Facturas F
				where F.codigoFactura = codFactura;
        End//
Delimiter ;
-- call sp_EliminarFactura(3);

-- BUSCAR FACTURA
Delimiter //
	create procedure sp_BuscarFactura (
		in codFactura int)
        Begin
			select
				F.codigoFactura, 
                F.fechaEmision, 
                F.total, 
                F.metodoPago, 
                F.codigoCliente, 
                F.codigoEmpleado
			from Facturas F
				where F.codigoFactura = codFactura;
        End//
Delimiter ;
-- call sp_BuscarFactura(2);

-- EDITAR FACTURA
Delimiter //
	create procedure sp_EditarFactura(
		in codFactura int,
        in fecEmision date,
        in totales decimal(10,2),
        in metPago varchar(15))
        Begin
			update Facturas F
				set
					F.fechaEmision = fecEmision, 
					F.total = totales, 
					F.metodoPago = metPago
                where F.codigoFactura = codFactura;
        End//
Delimiter ;
-- call sp_EditarFactura(2,'2025-03-09',1000.50,'Tarjeta');


-- ----------------- ENTIDAD COMPRAS --------------------------
-- AGREGAR COMPRA
Delimiter //
	create procedure sp_AgregarCompra (
		in fechaCompra date,
        in total decimal(10,2),
        in detalle varchar(255),
        in codigoProveedor int)
        Begin
			insert into Compras (fechaCompra, total, detalle, codigoProveedor)
				values (fechaCompra, total, detalle, codigoProveedor);
        End//
Delimiter ;
call sp_AgregarCompra('2025-04-10',50.50,'2 Simparica, 1 COMPLET PLUS',1);

-- LISTAR COMPRAS
Delimiter //
	create procedure sp_ListarCompras()
		Begin
			Select
				Com.codigoCompra, 
                Com.fechaCompra, 
                Com.total, 
                Com.detalle, 
                Com.codigoProveedor
			from Compras Com;
        End//
Delimiter ;
call sp_ListarCompras();

-- ELIMINAR COMPRA
Delimiter //
	create procedure sp_EliminarCompra(
		in codCompra int)
        Begin
			delete from Compras Com
				where Com.codigoCompra = codCompra;
        End//
Delimiter ;
-- call sp_EliminarCompra(3);

-- BUSCAR COMPRA
Delimiter //
	create procedure sp_BuscarCompra (
		in codCompra int)
        Begin
			select
				Com.codigoCompra, 
                Com.fechaCompra, 
                Com.total, 
                Com.detalle, 
                Com.codigoProveedor
			from Compras Com
				where Com.codigoCompra = codCompra;
        End//
Delimiter ;
-- call sp_BuscarCompra(2);

-- EDITAR COMPRA
Delimiter //
	create procedure sp_EditarCompra(
		in codCompra int,
        in fecCompra date,
        in ttals decimal(10,2),
        in detalles varchar(255))
        Begin
			update Compras Com
				set
					Com.fechaCompra = fecCompra, 
					Com.total = ttals, 
					Com.detalle = detalles
                where Com.codigoCompra = codCompra;
        End//
Delimiter ;
-- call sp_EditarCompra(2,'2025-04-09',25.50,'2 Simparica');

DELIMITER //
	create procedure sp_ReporteRecetas()
		begin
			select 
                m.nombreMascota,
                M.pesoActualKg,
                M.especie,
                timestampdiff(year, M.fechaNacimiento, CURDATE()) as edad,
                CONCAT(V.nombreVeterinario, ' ', V.apellidoVeterinario) AS Veterinario,
				med.nombreMedicamento,
				r.dosis,
				r.duracionDosis,
                r.indicaciones,
                r.frecuencia
			from Veterinarios v
			inner join Consultas c on v.codigoVeterinario = c.codigoVeterinario
            inner join Recetas r on r.codigoConsulta = c.codigoConsulta
            inner join Medicamentos med on med.codigoMedicamento= r.codigoMedicamento
            inner join Mascotas m on m.codigoMascota = c.codigoMascota
            order by r.codigoReceta;
		end//
DELIMITER ;
-- call sp_ReporteRecetas;


DELIMITER //
	create procedure sp_ReporteCarneVacunaciones()
		begin
			select 
				CONCAT(cli.nombreCliente, ' ', cli.apellidoCliente) AS Cliente,
				cli.telefonoCliente,
				m.nombreMascota,
				m.especie,
				CONCAT(v.nombreVeterinario, ' ', v.apellidoVeterinario) AS Veterinario,
				vcns.nombreVacuna,
				vac.observaciones,
				vac.fechaAplicacion
			from Veterinarios v
			inner join Vacunaciones vac on vac.codigoVeterinario = v.codigoVeterinario
			inner join Vacunas vcns on vac.codigoVacuna = vcns.codigoVacuna
			inner join Mascotas m on m.codigoMascota = vac.codigoMascota
			inner join Clientes cli on cli.codigoCliente = m.codigoCliente
			order by m.codigoMascota;
		end//
DELIMITER ;
-- call sp_ReporteCarneVacunaciones;


DELIMITER //
	create procedure sp_ReporteFacturas()
		begin
			select 
				f.codigoFactura,
				f.fechaEmision,
				f.metodoPago ,
				f.total,
				CONCAT(emp.nombreEmpleado, ' ', emp.apellidoEmpleado) as 'Empleado que atendió',
				CONCAT(cli.nombreCliente, ' ', cli.apellidoCliente) as 'Cliente',
				m.nombreMascota,
                m.especie,
                med.nombreMedicamento,
                med.precioUnitario
			from Clientes cli
            inner join Facturas f on cli.codigoCliente = f.codigoCliente
			inner join Empleados emp on f.codigoEmpleado = emp.codigoEmpleado
			inner join Mascotas m on cli.codigoCliente = m.codigoCliente
            inner join Consultas co on co.codigoMascota = m.codigoMascota
            inner join Recetas r on r.codigoConsulta = co.codigoConsulta
            inner join Medicamentos med on med.codigoMedicamento = r.codigoMedicamento
            order by m.codigoMascota;
			
		end//
DELIMITER ;
-- call sp_ReporteFacturas;




create table Usuario(
	codigoUsuario int not null auto_increment,
    nombreUsuario varchar(100) not null,
    apellidoUsuario varchar(100) not null,
    usuarioLogin varchar(50) not null,
    contrasena varchar(50) not null,
    primary key PK_codigoUsuario (codigoUsuario)
);

Delimiter //
	create procedure sp_AgregarUsuario( 
        in nombreUsuario varchar(100), 
        in apellidoUsuario varchar(100),
        in usuarioLogin varchar(50), 
        in contrasena varchar(50))
        Begin
			insert into Usuario(codigoUsuario, nombreUsuario, apellidoUsuario, usuarioLogin, contrasena)
				values (codigoUsuario, nombreUsuario, apellidoUsuario, usuarioLogin, contrasena);
        End//
Delimiter ;
call sp_AgregarUsuario('Alejandro','Echeverria','aeche','12345');

Delimiter //
	create procedure sp_ListarUsuarios()
		Begin
			select
				U.codigoUsuario,
                U.nombreUsuario,
                U.apellidoUsuario,
                U.usuarioLogin,
                U.contrasena
			from Usuario U;
        End//
Delimiter ; 
call sp_ListarUsuarios;

create table Login(
	usuarioMaster varchar(50) not null,
    passwordLogin varchar(50) not null,
    primary key PK_usuarioMaster (usuarioMaster)
);

-- ----------------------------------------------------------------------------------
call sp_AgregarCliente('Alejandro Hugo','Camey Flores','66666666','6 Av 13-54 Z-7 Col Landiv','acamey@kinal.edu.gt','2025-04-09');
call sp_AgregarCliente('María José', 'Pérez García', '12345678', '3a Calle 10-25 Z-3 Col San Juan', 'mperez@gmail.com', '2025-03-21');
call sp_AgregarCliente('Carlos Antonio', 'Ramírez Soto', '87654321', '5a Avenida 4-66 Z-12 Mixco', 'c.ramirez@hotmail.com', '2025-04-01');
call sp_AgregarCliente('Lucía Fernanda', 'Gómez Ruiz', '11223344', '7a Av 2-12 Z-1 Centro', 'lucia.gomez@yahoo.com', '2025-02-15');
call sp_AgregarCliente('Jorge Luis', 'Martínez López', '33445566', '9 Calle 6-78 Z-5 El Carmen', 'jl.martinez@correo.com', '2025-03-10');
call sp_AgregarCliente('Ana Sofía', 'Hernández Cruz', '99887766', '2a Calle 14-22 Z-9 Vista Hermosa', 'asofia@kinal.edu.gt', '2025-01-30');
call sp_AgregarCliente('Luis Eduardo', 'Chávez Méndez', '44556677', '10 Av 18-10 Z-6 San Cristóbal', 'lechavez@correo.com', '2025-05-05');
call sp_AgregarCliente('Gabriela Elena', 'Morales Díaz', '55443322', '6 Calle 9-19 Z-2 Jardines de la Asunción', 'gmorales@gmail.com', '2025-02-28');
call sp_AgregarCliente('Ricardo Javier', 'Ortiz Ramírez', '77889900', '4a Av 15-60 Z-8 Villa Linda', 'rj.ortiz@hotmail.com', '2025-03-18');
call sp_AgregarCliente('Paola Andrea', 'López Cano', '66778899', '1a Calle 20-30 Z-4 El Progreso', 'paola.lopez@kinal.edu.gt', '2025-04-20');
call sp_ListarClientes();
call sp_EditarCliente(1,'Alejandro Hugo','Camey Flores','59806715','6 Av 13-54 Z-7 Col Landiv','acamey@gmail.com','2025-04-09');
call sp_BuscarCliente(1);
-- call sp_EliminarCliente(1);call sp_EliminarCliente(1)
-- ----------------------------------------------------------------------------------
call sp_AgregarVeterinario('Cristian Pablo','Tax Cajchum','Cirujano','58462318','ctax@kinal.edu.gt','2025-01-01','Activo');
call sp_AgregarVeterinario('María Fernanda', 'López Gómez', 'Anestesista', '11224488', 'mlopez@kinal.edu.gt', '2025-01-05', 'Activo');
call sp_AgregarVeterinario('Luis Alberto', 'Ramírez Choc', 'Dermatólogo', '22335566', 'lramirez@kinal.edu.gt', '2025-01-10', 'Inactivo');
call sp_AgregarVeterinario('Andrea Paola', 'García Cano', 'Cardiólogo', '33446677', 'agarcia@kinal.edu.gt', '2025-02-01', 'Activo');
call sp_AgregarVeterinario('Carlos Daniel', 'Pérez Díaz', 'Oftalmólogo', '44557788', 'cperez@kinal.edu.gt', '2025-02-15', 'Activo');
call sp_AgregarVeterinario('Lucía Elena', 'Méndez Ruano', 'Radiólogo', '55668899', 'lmendez@kinal.edu.gt', '2025-03-01', 'Inactivo');
call sp_AgregarVeterinario('Jorge Emmanuel', 'Soto López', 'Cirujano', '66779900', 'jsoto@kinal.edu.gt', '2025-03-15', 'Activo');
call sp_AgregarVeterinario('Paola Andrea', 'Castro Molina', 'Nutriólogo', '77880011', 'pcastro@kinal.edu.gt', '2025-04-01', 'Activo');
call sp_AgregarVeterinario('Brenda Sofía', 'Ortiz Reyes', 'Etólogo', '88991122', 'bortiz@kinal.edu.gt', '2025-04-10', 'Inactivo');
call sp_AgregarVeterinario('Fernando José', 'Chávez Tzic', 'Anestesista', '99002233', 'fchavez@kinal.edu.gt', '2025-04-20', 'Activo');
call sp_ListarVeterinarios();
call sp_EditarVeterinario(1,'Cristian Pablo','Tax Cajchum','Cirujano','58462318','ctax@gmail.com','2025-01-01','Inactivo');
call sp_BuscarVeterinario(1);
-- call sp_EliminarVeterinario(1);
-- ----------------------------------------------------------------------------------
call sp_AgregarProveedor('Patitos Pet','30 calle 06-15 zona 11 Colonia Granai & Towson I, Guatemala','24685135','patitospet@gmail.com');
call sp_AgregarProveedor('Mascotas y Más', '5a avenida 10-24 zona 1, Ciudad de Guatemala', '22334455', 'mascotasyymas@gmail.com');
call sp_AgregarProveedor('VetMarket Pro', 'Avenida Petapa 32-45 zona 12, Guatemala', '33445566', 'ventas@vetmarketpro.com');
call sp_AgregarProveedor('Animal Center GT', 'Calzada Roosevelt 18-22 zona 7, Guatemala', '55667788', 'contacto@animalcentergt.com');
call sp_AgregarProveedor('Distribuidora El Arca', '9a calle 5-10 zona 3, Mixco', '66778899', 'elarca@correo.com');
call sp_AgregarProveedor('ZooStock', 'Carretera a El Salvador km 16.5 Condominio Los Olivos', '77889900', 'info@zoostock.com');
call sp_AgregarProveedor('PetWorld Guatemala', 'Boulevard Liberación 21-33 zona 9, Guatemala', '88990011', 'servicio@petworldgt.com');
call sp_AgregarProveedor('SuperVet S.A.', '4a avenida 12-45 zona 5, Villa Nueva', '99001122', 'ventas@supervetsa.com');
call sp_AgregarProveedor('Animalis Distribuciones', '6a calle 3-22 zona 10, Guatemala', '11223344', 'ventas@animalis.com.gt');
call sp_AgregarProveedor('Mascota Feliz', 'Centro Comercial Metroplaza, local 23 zona 17', '22335577', 'contacto@mascotafeliz.gt');
call sp_ListarProveedores();
call sp_EditarProveedor(1,'Patitos Pet','30 calle 06-15 zona 11 Colonia Granai & Towson I, Guatemala','24685135','patitopet@gmail.com');
call sp_BuscarProveedor(1);
-- call sp_EliminarProveedor(1);
-- ----------------------------------------------------------------------------------
call sp_AgregarVacuna('Vacuna antirrábica','Vacuna contra la infección de la rabia','1 dosis cada tres meses',5);
call sp_AgregarVacuna('Vacuna quíntuple', 'Protege contra moquillo, hepatitis, parvovirus, parainfluenza y leptospira', '1 dosis anual', 10);
call sp_AgregarVacuna('Vacuna triple felina', 'Protege a gatos contra rinotraqueítis, calicivirus y panleucopenia', '1 dosis cada 12 meses', 8);
call sp_AgregarVacuna('Vacuna contra leptospirosis', 'Previene infecciones bacterianas por leptospira', '1 dosis semestral', 7);
call sp_AgregarVacuna('Vacuna contra parvovirus canino', 'Protege contra parvovirus, altamente contagioso en perros', '1 dosis inicial y refuerzo anual', 6);
call sp_AgregarVacuna('Vacuna contra coronavirus canino', 'Protección contra infección intestinal causada por coronavirus', '1 dosis inicial y refuerzo anual', 4);
call sp_AgregarVacuna('Vacuna bordetella', 'Previene la tos de las perreras (bordetella bronchiseptica)', '1 dosis cada 6 meses', 5);
call sp_AgregarVacuna('Vacuna contra giardiasis', 'Protección contra el parásito Giardia', '1 dosis con refuerzo a las 3 semanas', 3);
call sp_AgregarVacuna('Vacuna contra lyme', 'Previene la enfermedad de Lyme causada por garrapatas', '1 dosis anual', 6);
call sp_AgregarVacuna('Vacuna contra leucemia felina', 'Protege a gatos contra el virus de la leucemia felina (FeLV)', '2 dosis iniciales y refuerzo anual', 9);
call sp_ListarVacunas();
call sp_EditarVacuna(1,'Vacuna antirrábica','Vacuna contra la infección de la rabia','1 dosis cada seis meses','5');
call sp_BuscarVacuna(1);
-- call sp_EliminarVacuna(1);
-- ----------------------------------------------------------------------------------
call sp_AgregarEmpleado('Mauricio Oswald','Chamale Kikibix','Gerente','31567925','ochamale@gmail.com');
call sp_AgregarEmpleado('Carla Viviana', 'Sicán López', 'Recepcionista', '22334455', 'csican@kinal.edu.gt');
call sp_AgregarEmpleado('José Estuardo', 'Tzoc Pérez', 'Veterinario', '33445566', 'jtzoc@kinal.edu.gt');
call sp_AgregarEmpleado('Andrea Paola', 'Mejía Castillo', 'Asistente', '44556677', 'amejia@kinal.edu.gt');
call sp_AgregarEmpleado('Luis Fernando', 'Chub Tzunún', 'Encargado de bodega', '55667788', 'lchub@kinal.edu.gt');
call sp_AgregarEmpleado('Patricia Elena', 'Solares Rivera', 'Contadora', '66778899', 'psolares@kinal.edu.gt');
call sp_AgregarEmpleado('Daniel Ignacio', 'Cuxum Cúmez', 'Administrador', '77889900', 'dcuxum@kinal.edu.gt');
call sp_AgregarEmpleado('Melanie Raquel', 'García Mux', 'Secretaria', '88990011', 'mgarcia@kinal.edu.gt');
call sp_AgregarEmpleado('Juan Pablo', 'Raxón Tzic', 'Mensajero', '99001122', 'jpraxon@kinal.edu.gt');
call sp_AgregarEmpleado('Sofía Carolina', 'Chajón Tuy', 'Auxiliar de limpieza', '11223344', 'schajon@kinal.edu.gt');
call sp_ListarEmpleados();
call sp_EditarEmpleado('1','Mauricio Oswald','Chamale Kikibix','Limpieza','31567925','ochamale@gmail.com');
call sp_BuscarEmpleado(1);
-- call sp_EliminarEmpleado(1);
-- ----------------------------------------------------------------------------------
call sp_AgregarMascota('Rocky','Perro','Pastor Aleman','Macho','2021-05-22','Cafe','30.9',1);
call sp_AgregarMascota('Astro','Perro','Pastor Aleman','Macho','2021-05-22','Cafe','40.5',1);
call sp_AgregarMascota('Oso','Perro','Pastor Aleman','Macho','2021-05-22','Cafe','35.2',1);
call sp_AgregarMascota('Luna', 'Gato', 'Siamés', 'Hembra', '2022-03-15', 'Blanco con gris', '4.2', 2);
call sp_AgregarMascota('Max', 'Perro', 'Labrador Retriever', 'Macho', '2020-11-10', 'Negro', '28.5', 3);
call sp_AgregarMascota('Mía', 'Conejo', 'Mini Lop', 'Hembra', '2023-01-08', 'Gris claro', '2.1', 4);
call sp_AgregarMascota('Toby', 'Perro', 'Bulldog Francés', 'Macho', '2021-07-03', 'Beige', '12.4', 5);
call sp_AgregarMascota('Nina', 'Gato', 'Persa', 'Hembra', '2022-09-20', 'Blanco', '3.9', 6);
call sp_AgregarMascota('Simba', 'Perro', 'Golden Retriever', 'Macho', '2020-05-05', 'Dorado', '32.0', 7);
call sp_AgregarMascota('Lola', 'Perro', 'Chihuahua', 'Hembra', '2023-06-12', 'Café claro', '2.8', 8);
call sp_AgregarMascota('Thor', 'Gato', 'Maine Coon', 'Macho', '2021-02-25', 'Naranja', '6.7', 9);
call sp_AgregarMascota('Kiara', 'Perro', 'Poodle Toy', 'Hembra', '2023-04-17', 'Blanco', '4.5', 10);
call sp_ListarMascotas();
call sp_EditarMascota(1,'Rocky','Perro','Chihuahua','Macho','2021-05-22','Cafe','30.9');
call sp_BuscarMascota(1);
-- call sp_EliminarMascota(1);
-- ----------------------------------------------------------------------------------

call sp_AgregarConsulta('2025-04-13 14:30:00','Falta de Apetito','Infección gastrointestinal leve','Se recomienda dieta blanda por 3 dias, mantener hidratación',1,1);
call sp_AgregarConsulta('2025-04-14 09:00:00', 'Tos persistente', 'Bronquitis leve', 'Administrar jarabe para la tos por 7 días', 2, 2);
call sp_AgregarConsulta('2025-04-15 11:15:00', 'Cojera en pata trasera', 'Esguince leve', 'Reposo y antiinflamatorios por 10 días', 3, 3);
call sp_AgregarConsulta('2025-04-15 16:45:00', 'Pérdida de peso', 'Desnutrición leve', 'Dieta hipercalórica y suplementos vitamínicos', 4, 4);
call sp_AgregarConsulta('2025-04-16 08:30:00', 'Picazón en piel', 'Dermatitis alérgica', 'Aplicar crema antihistamínica y baño medicado', 5, 5);
call sp_AgregarConsulta('2025-04-16 13:00:00', 'Vómitos frecuentes', 'Gastritis', 'Evitar alimentos irritantes y administrar antieméticos', 6, 6);
call sp_AgregarConsulta('2025-04-17 10:00:00', 'Letargo y debilidad', 'Anemia leve', 'Suplementos de hierro y alimentación balanceada', 7, 7);
call sp_AgregarConsulta('2025-04-18 15:30:00', 'Inflamación en articulación', 'Artritis', 'Reposo y medicación antiinflamatoria', 8, 8);
call sp_AgregarConsulta('2025-04-19 09:45:00', 'Secreción nasal', 'Resfriado común', 'Mantener hidratación y administrar descongestionantes', 9, 9);
call sp_AgregarConsulta('2025-04-20 14:00:00', 'Diarrea intermitente', 'Infección intestinal', 'Administrar antibióticos y dieta blanda', 10, 10);
call sp_ListarConsultas();
call sp_EditarConsulta(1,'2025-04-13 14:30:00','Falta de Apetito','Infección gastrointestinal Grave','Se recomienda dieta blanda por 3 dias, mantener hidratación');
call sp_BuscarConsulta(1);
-- call sp_EliminarConsulta(1);

-- ----------------------------------------------------------------------------------
call sp_AgregarTratamiento('Controlar infección gastrointestinal y rehidritar el paciente','2025-04-13','2025-08-01','Suero oral 50ml cada 6 horas',1);
call sp_AgregarTratamiento('Administrar jarabe para la tos por 7 días', '2025-04-14', '2025-04-21', 'Jarabe para la tos 10ml cada 8 horas', 2);
call sp_AgregarTratamiento('Reposo y antiinflamatorios por 10 días', '2025-04-15', '2025-04-25', 'Ibuprofeno 5mg/kg cada 12 horas', 3);
call sp_AgregarTratamiento('Dieta hipercalórica y suplementos vitamínicos', '2025-04-15', '2025-05-15', 'Suplementos vitamínicos 1 cápsula diaria', 4);
call sp_AgregarTratamiento('Aplicar crema antihistamínica y baño medicado', '2025-04-16', '2025-04-23', 'Crema antihistamínica dos veces al día', 5);
call sp_AgregarTratamiento('Evitar alimentos irritantes y administrar antieméticos', '2025-04-16', '2025-04-22', 'Antiemético 5mg/kg cada 8 horas', 6);
call sp_AgregarTratamiento('Suplementos de hierro y alimentación balanceada', '2025-04-17', '2025-05-17', 'Suplemento de hierro 1 vez al día', 7);
call sp_AgregarTratamiento('Reposo y medicación antiinflamatoria', '2025-04-18', '2025-05-02', 'Antiinflamatorio 10mg/kg cada 12 horas', 8);
call sp_AgregarTratamiento('Mantener hidratación y administrar descongestionantes', '2025-04-19', '2025-04-26', 'Descongestionante 1 dosis diaria', 9);
call sp_AgregarTratamiento('Administrar antibióticos y dieta blanda', '2025-04-20', '2025-05-05', 'Antibiótico 20mg/kg cada 8 horas', 10);
call sp_ListarTratamientos();
call sp_EditarTratamiento(1,'Controlar infección gastrointestinal y rehidritar el paciente','2025-04-13','2025-08-01','Suero oral 100ml cada 2 horas');
call sp_BuscarTratamiento(1);
-- call sp_EliminarTratamiento(1);

-- ----------------------------------------------------------------------------------
call sp_AgregarCita('2025-04-15', '10:00:00', 'Consulta general', 'Completada', 2, 2);
call sp_AgregarCita('2025-04-16', '09:00:00', 'Revisión anual', 'Pendiente', 3, 3);
call sp_AgregarCita('2025-04-17', '11:30:00', 'Vacunación', 'Completada', 4, 4);
call sp_AgregarCita('2025-04-18', '14:00:00', 'Consulta dental', 'Pendiente', 5, 5);
call sp_AgregarCita('2025-04-19', '08:15:00', 'Control de peso', 'Cancelada', 6, 6);
call sp_AgregarCita('2025-04-20', '13:45:00', 'Chequeo general', 'Pendiente', 7, 7);
call sp_AgregarCita('2025-04-21', '15:00:00', 'Consulta dermatológica', 'Completada', 8, 8);
call sp_AgregarCita('2025-04-22', '10:30:00', 'Control de vacunas', 'Pendiente', 9, 9);
call sp_AgregarCita('2025-04-23', '16:20:00', 'Revisión postoperatoria', 'Pendiente', 10, 10);
call sp_AgregarCita('2025-04-24', '12:00:00', 'Consulta general', 'Completada', 1, 1);
call sp_ListarCitas();
call sp_EditarCita('1','2025-04-14','16:25:50','Infección gastrointestinal Leve','Cancelada');
call sp_BuscarCita(1);
-- call sp_EliminarCita(1);
-- ----------------------------------------------------------------------------------
call sp_AgregarVacunaciones('2025-05-01','Vacuna bien tolerada, sin síntomas',1,1,1);
call sp_AgregarVacunaciones('2025-05-01','Vacuna con un poco de malestar',1,1,1);
call sp_AgregarVacunaciones('2025-05-15','Leve somnolencia post vacuna',1,2,1);
call sp_AgregarVacunaciones('2025-05-29','Sin efectos secundarios',1,3,1);
call sp_AgregarVacunaciones('2025-05-02','Inflamación leve en el sitio de inyección',2,1,2);
call sp_AgregarVacunaciones('2025-05-16','Sin reacción adversa observada',2,2,2);
call sp_AgregarVacunaciones('2025-05-30','Comportamiento normal posterior a la vacuna',2,3,2);
call sp_AgregarVacunaciones('2025-05-03','Reacción leve: somnolencia',3,1,3);
call sp_AgregarVacunaciones('2025-05-17','Vacunación exitosa',3,2,3);
call sp_AgregarVacunaciones('2025-05-31','Sin anomalías post vacuna',3,3,3);
call sp_AgregarVacunaciones('2025-04-13','El perro(Rocky) reacciono positivamente a la vacuna',1,1,1);
call sp_AgregarVacunaciones('2025-04-14', 'Sin efectos secundarios tras la vacuna', 2, 2, 2);
call sp_AgregarVacunaciones('2025-04-15', 'Reacción leve: picazón en el lugar de la inyección', 3, 3, 3);
call sp_AgregarVacunaciones('2025-04-16', 'Vacuna bien tolerada, sin complicaciones', 4, 4, 4);
call sp_AgregarVacunaciones('2025-04-17', 'Leve fiebre post vacuna, monitoreo recomendado', 5, 5, 5);
call sp_AgregarVacunaciones('2025-04-18', 'Ninguna reacción adversa observada', 6, 6, 6);
call sp_AgregarVacunaciones('2025-04-19', 'Pequeña inflamación local, sin mayor gravedad', 7, 7, 7);
call sp_AgregarVacunaciones('2025-04-20', 'Reacción normal, sin síntomas adicionales', 8, 8, 8);
call sp_AgregarVacunaciones('2025-04-21', 'Buen estado general después de la vacunación', 9, 9, 9);
call sp_AgregarVacunaciones('2025-04-22', 'Vacunación exitosa, sin efectos secundarios', 10, 10, 10);
call sp_ListarVacunaciones();
call sp_EditarVacunaciones('1','2025-04-13','El perro(Rocky) reacciono negativamente a la vacuna');
call sp_BuscarVacunaciones(1);
-- call sp_EliminarVacunacion(1);
-- ----------------------------------------------------------------------------------
call sp_AgregarMedicamento('MediRab','Medicina contra la rabia','15','350.00','2025-5-01',1);
call sp_AgregarMedicamento('AntibioVet', 'Antibiótico para infecciones bacterianas', '10', '500.00', '2025-05-02', 2);
call sp_AgregarMedicamento('CalmVet', 'Analgésico y antiinflamatorio', '20', '280.00', '2025-05-03', 3);
call sp_AgregarMedicamento('VitamiCan', 'Suplemento vitamínico para perros', '30', '150.00', '2025-05-04', 4);
call sp_AgregarMedicamento('Desparax', 'Medicamento antiparasitario', '25', '400.00', '2025-05-05', 5);
call sp_AgregarMedicamento('PediVet', 'Vacuna pediátrica para cachorros', '15', '600.00', '2025-05-06', 6);
call sp_AgregarMedicamento('CardioVet', 'Medicamento para problemas cardíacos', '12', '750.00', '2025-05-07', 7);
call sp_AgregarMedicamento('AntiFung', 'Tratamiento antifúngico tópico', '18', '350.00', '2025-05-08', 8);
call sp_AgregarMedicamento('ImmunoVet', 'Inmunoestimulante para mascotas', '22', '670.00', '2025-05-09', 9);
call sp_AgregarMedicamento('RegenPet', 'Suplemento para regeneración articular', '16', '800.00', '2025-05-10', 10);
call sp_ListarMedicamentos();
call sp_EditarMedicamento('1','MediRab','Medicina contra la rabia','15','350.00','2020-01-01');
call sp_BuscarMedicamento(1);
-- call sp_EliminarMedicamento(1);
-- ----------------------------------------------------------------------------------
call sp_AgregarReceta('8 mg/kg de peso corporal', 'Administrar cada 24 horas', 5, 'Agitar antes de usar', 1, 1);
call sp_AgregarReceta('12 ml diarios', 'Dividir en dos tomas', 10, 'No administrar con leche', 1, 3);
call sp_AgregarReceta('0.2 mg/kg', 'Administrar en ayunas', 3, 'Evitar cambios bruscos de temperatura', 1, 7);
call sp_AgregarReceta('1 ml por cada 5 kg', 'Cada 8 horas', 6, 'Mantener fuera del alcance de niños', 2, 3);
call sp_AgregarReceta('5 gotas por kg', 'Cada 6 horas', 8, 'No mezclar con agua', 2, 4);
call sp_AgregarReceta('20 ml', 'Una vez al día', 7, 'No congelar', 2, 5);
call sp_AgregarReceta('3-5 ml', 'Cada 4 horas', 5, 'Agitar antes de usar', 3, 8);
call sp_AgregarReceta('6 mg/kg', 'Cada 12 horas', 12, 'Evitar alimentos grasos', 3, 9);
call sp_AgregarReceta('1 gota/kg', 'Cada 24 horas', 9, 'Aplicar con jeringa dosificadora', 3, 6);
call sp_AgregarReceta('20 mg/kg de peso corporal', 'Administrar cada 8 horas', 12, 'No administrar con otros medicamentos', 4, 4);
call sp_AgregarReceta('1-2 tabletas según peso', 'Administrar una vez al día', 7, 'No exceder la dosis recomendada', 5, 5);
call sp_AgregarReceta('0.5-1 ml por vía oral', 'Administrar cada 12 horas', 8, 'Mantener en lugar fresco y seco', 6, 6);
call sp_AgregarReceta('15-30 mg/kg de peso corporal', 'Administrar dos veces al día', 20, 'No usar en embarazo', 7, 7);
call sp_AgregarReceta('5 ml por vía subcutánea', 'Administrar una vez al día', 18, 'Evitar contacto con ojos', 8, 8);
call sp_AgregarReceta('10-25 mg/kg de peso corporal', 'Administrar cada 6 horas', 14, 'Conservar refrigerado', 9, 9);
call sp_AgregarReceta('1-3 gotas según peso', 'Administrar cada 4 horas', 5, 'Evitar contacto con heridas abiertas', 10, 10);
call sp_ListarRecetas();
call sp_EditarReceta(1,'100-200 mg/kg de peso corporal','Administrar dos veces al dia',7,'Administrar con o sin comida');
call sp_BuscarReceta(1);
-- call sp_EliminarReceta(1);
-- ----------------------------------------------------------------------------------
call sp_AgregarFactura('2025-05-08','Efectivo',1,1);
call sp_AgregarFactura('2025-05-09', 'Tarjeta', 2, 2);
call sp_AgregarFactura('2025-05-10', 'Efectivo', 3, 3);
call sp_AgregarFactura('2025-05-11', 'Transferencia', 4, 4);
call sp_AgregarFactura('2025-05-12', 'Tarjeta', 5, 5);
call sp_AgregarFactura('2025-05-13', 'Efectivo', 6, 6);
call sp_AgregarFactura('2025-05-14', 'Transferencia', 7, 7);
call sp_AgregarFactura('2025-05-15', 'Tarjeta', 8, 8);
call sp_AgregarFactura('2025-05-16', 'Efectivo', 9, 9);
call sp_AgregarFactura('2025-05-17', 'Transferencia', 10, 10);
call sp_ListarFacturas();
call sp_EditarFactura(1,'2025-05-08','1380','Efectivo');
call sp_EditarFactura(2,'2025-05-08','730','Tarjeta');
call sp_EditarFactura(3,'2025-05-08','1620','Tarjeta');
call sp_BuscarFactura(1);
-- call sp_EliminarFactura(1);

-- ----------------------------------------------------------------------------------
call sp_AgregarCompra('2025-04-09','12345678.00','2 Metronidazol de 50mg',1);
call sp_AgregarCompra('2025-04-10', '98765432.00', '5 Amoxicilina de 250mg', 2);
call sp_AgregarCompra('2025-04-11', '45678912.00', '3 Ibuprofeno de 200mg', 3);
call sp_AgregarCompra('2025-04-12', '23456789.00', '10 Suplemento vitamínico', 4);
call sp_AgregarCompra('2025-04-13', '87654321.00', '7 Desparasitante oral', 5);
call sp_AgregarCompra('2025-04-14', '34567890.00', '4 Vacuna antirrábica', 6);
call sp_AgregarCompra('2025-04-15', '76543210.00', '6 Medicamento para alergias', 7);
call sp_AgregarCompra('2025-04-16', '56789012.00', '8 Crema antifúngica', 8);
call sp_AgregarCompra('2025-04-17', '65432109.00', '2 Suero oral', 9);
call sp_AgregarCompra('2025-04-18', '43210987.00', '5 Jarabe para la tos', 10);
call sp_ListarCompras();
call sp_EditarCompra(1,'2025-04-09','400.00','2 Metronidazol de 500mg');
call sp_BuscarCompra(1);
-- call sp_EliminarCompra(1);
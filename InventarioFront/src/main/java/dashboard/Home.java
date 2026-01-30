/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dashboard;

import Utileria.RoundedButton;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import inventario.Inventario;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author JMorales
 */
public class Home extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Home.class.getName());

    private final Color COLOR_BACKGROUND_NORMAL = new Color(250, 249, 246);
    private final Color COLOR_BACKGROUND_HOVER = new Color(224, 224, 224);
    private final Color COLOR_SELECTED_BG = new Color(19, 80, 125);
    private final Color COLOR_SELECTED_FG = new Color(253, 253, 253);
    private final Color COLOR_TEXT_NORMAL = new Color(51, 51, 51);

    private final Dimension DIMENSION_BTN_SUBMENU = new Dimension(220, 40);
    private final Dimension ALTURA_BOTON_PRINCIPAL = new Dimension(240, 60);

    private boolean menuOrganizacionDesplegado = false;
    private boolean menuConfiguracionDesplegado = false;
    private JButton botonActual = null;

    private final JButton btnEmpresa = new RoundedButton("Empresas", 10);
    private final JButton btnSucursal = new RoundedButton("Sucursales", 10);
    private final JButton btnDepartamento = new RoundedButton("Departamentos", 10);
    private final JButton btnPuesto = new RoundedButton("Puestos", 10);
    private final JButton btnUsuario = new RoundedButton("Usuarios", 10);
    private final JButton btnAuditoria = new RoundedButton("Auditoría", 10);

    private javax.swing.Box wrapperEmpresa;
    private javax.swing.Box wrapperSucursal;
    private javax.swing.Box wrapperDepartamento;
    private javax.swing.Box wrapperPuesto;
    private javax.swing.Box wrapperUsuario;
    private javax.swing.Box wrapperAuditoria;

    private Component gapEmpresa;
    private Component gapSucursal;
    private Component gapDepartamento;
    private Component gapUsuario;
    private Component gapOrganizacion;
    private Component gapConfiguracion;

    /**
     * Creates new form Home
     */
    public Home() {
        initComponents();

        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        this.setTitle("Sistema de Inventario - v1.0");

        initEstructuraMenu();

        initEstilosVisuales();

        agregarEstilos();
    }

    private void agregarEstilos() {

        // 1. ELIMINAR O COMENTAR EL BLOQUE DE NIMBUS QUE NETBEANS GENERA AUTOMÁTICAMENTE
        /* try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (...) { ... } 
         */
        // 2. CONFIGURAR EL TEMA DE FLATLAF AQUÍ
        try {
            // Opción A: Tema Oscuro (Dark)
//            com.formdev.flatlaf.FlatDarkLaf.setup();

            // Opción B: Tema Claro (Light)
            com.formdev.flatlaf.FlatLightLaf.setup();
            // Opción C: Tema tipo IntelliJ o Darcula
            // com.formdev.flatlaf.FlatIntelliJLaf.setup();
            // com.formdev.flatlaf.FlatDarculaLaf.setup();
            // Opción D: Temas Extras (requiere la dependencia flatlaf-intellij-themes)
            // com.formdev.flatlaf.intellijthemes.FlatNordIJTheme.setup();
            // com.formdev.flatlaf.intellijthemes.materialthemeutil.FlatMaterialDeepOceanIJTheme.setup();
        } catch (Exception ex) {
            System.err.println("Error al iniciar el tema");
        }

    }

    /**
     * Inicializa las estructuras necesarias para los menús desplegables. Crea
     * los wrappers con sangría y los componentes de espaciado (gaps).
     */
    private void initEstructuraMenu() {
        wrapperEmpresa = prepararBotonConSangria(btnEmpresa);
        wrapperSucursal = prepararBotonConSangria(btnSucursal);
        wrapperDepartamento = prepararBotonConSangria(btnDepartamento);
        wrapperPuesto = prepararBotonConSangria(btnPuesto);
        wrapperUsuario = prepararBotonConSangria(btnUsuario);
        wrapperAuditoria = prepararBotonConSangria(btnAuditoria);

        gapEmpresa = Box.createRigidArea(new Dimension(0, 5));
        gapSucursal = Box.createRigidArea(new Dimension(0, 5));
        gapDepartamento = Box.createRigidArea(new Dimension(0, 5));
        gapUsuario = Box.createRigidArea(new Dimension(0, 5));
        gapOrganizacion = Box.createRigidArea(new Dimension(0, 5));
        gapConfiguracion = Box.createRigidArea(new Dimension(0, 5));
    }

    /**
     * Configura iconos, fuentes y dimensiones de todos los botones.
     */
    private void initEstilosVisuales() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/logo.png")));

        // Icono usuario superior
        miniUser.setIcon(crearIconoColoreado("iconos/svgs/solid/circle-user.svg", 40, 40, COLOR_SELECTED_BG));

        // Configuración de Botones Principales
        configurarBotonMenu(btnHome, "iconos/svgs/regular/house.svg");
        configurarBotonMenu(btnInventario, "iconos/svgs/regular/clipboard.svg");
        configurarBotonMenu(btnTrabajadores, "iconos/svgs/regular/address-book.svg");
        configurarBotonMenu(btnAsignaciones, "iconos/svgs/regular/handshake.svg");
        configurarBotonMenu(btnSubMenuOrganizacion, "iconos/svgs/organization.svg");
        configurarBotonMenu(subMenuConfiguracion, "iconos/svgs/solid/gear.svg");

        // Configuración de Sub-Botones
        Font fuenteBase = btnHome.getFont();
        configurarSubBoton(btnEmpresa, "iconos/svgs/solid/building.svg", fuenteBase);
        configurarSubBoton(btnSucursal, "iconos/svgs/regular/map.svg", fuenteBase);
        configurarSubBoton(btnDepartamento, "iconos/svgs/layer-group.svg", fuenteBase);
        configurarSubBoton(btnPuesto, "iconos/svgs/briefcase.svg", fuenteBase);
        configurarSubBoton(btnUsuario, "iconos/svgs/regular/user.svg", fuenteBase);
        configurarSubBoton(btnAuditoria, "iconos/svgs/regular/pen-to-square.svg", fuenteBase);

        // Ajustes del Scroll
        scrollMenu.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollMenu.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(16);

        // Altura unificada
        aplicarAlturaBotones();
    }

    /**
     * Aplica una altura fija a todos los botones del menú para mantener
     * consistencia.
     */
    private void aplicarAlturaBotones() {
        JButton[] botones = {
            btnHome, btnTrabajadores, btnInventario, btnAsignaciones,
            btnSubMenuOrganizacion, subMenuConfiguracion,
            btnEmpresa, btnSucursal, btnDepartamento, btnPuesto, btnUsuario, btnAuditoria
        };

        for (JButton btn : botones) {
            btn.setPreferredSize(ALTURA_BOTON_PRINCIPAL);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelDeContenido = new javax.swing.JPanel();
        barraLateral = new javax.swing.JPanel();
        panelSuperior = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        logo = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        titulo = new javax.swing.JLabel();
        subTitulo = new javax.swing.JLabel();
        pnlMenu = new javax.swing.JPanel();
        scrollMenu = new javax.swing.JScrollPane();
        panelInterno = new javax.swing.JPanel();
        btnHome = new RoundedButton("Home", 15);
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10));
        btnTrabajadores = new RoundedButton("Trabajadores", 15);
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10));
        btnInventario = new RoundedButton("Inventario",15);
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10));
        btnAsignaciones = new RoundedButton("Asignaciones", 15);
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10));
        btnSubMenuOrganizacion = new RoundedButton("Organización", 15);
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10));
        subMenuConfiguracion = new RoundedButton("Configuración", 15);
        piePagina = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        miniUser = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        tipoUser = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 255));

        panelDeContenido.setBackground(new java.awt.Color(250, 249, 246));
        panelDeContenido.setToolTipText("");

        javax.swing.GroupLayout panelDeContenidoLayout = new javax.swing.GroupLayout(panelDeContenido);
        panelDeContenido.setLayout(panelDeContenidoLayout);
        panelDeContenidoLayout.setHorizontalGroup(
            panelDeContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 975, Short.MAX_VALUE)
        );
        panelDeContenidoLayout.setVerticalGroup(
            panelDeContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 980, Short.MAX_VALUE)
        );

        getContentPane().add(panelDeContenido, java.awt.BorderLayout.CENTER);

        barraLateral.setBackground(new java.awt.Color(234, 244, 251));
        barraLateral.setAutoscrolls(true);
        barraLateral.setPreferredSize(new java.awt.Dimension(280, 490));
        barraLateral.setLayout(new java.awt.BorderLayout());

        panelSuperior.setBackground(new java.awt.Color(250, 249, 246));
        panelSuperior.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 2, new java.awt.Color(209, 213, 219)));
        panelSuperior.setPreferredSize(new java.awt.Dimension(280, 140));
        panelSuperior.setLayout(new javax.swing.BoxLayout(panelSuperior, javax.swing.BoxLayout.LINE_AXIS));

        jPanel4.setBackground(new java.awt.Color(250, 249, 246));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 20, 30, 18));
        jPanel4.setPreferredSize(new java.awt.Dimension(90, 140));
        jPanel4.setLayout(new java.awt.GridLayout(1, 0));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logo.png"))); // NOI18N
        jPanel4.add(logo);

        panelSuperior.add(jPanel4);

        jPanel6.setBackground(new java.awt.Color(250, 249, 246));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 20, 1));
        jPanel6.setMinimumSize(new java.awt.Dimension(170, 82));
        jPanel6.setLayout(new java.awt.GridLayout(2, 0));

        titulo.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        titulo.setForeground(new java.awt.Color(0, 0, 0));
        titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo.setText("Sistema TI");
        titulo.setPreferredSize(new java.awt.Dimension(126, 50));
        jPanel6.add(titulo);

        subTitulo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        subTitulo.setForeground(new java.awt.Color(0, 0, 0));
        subTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        subTitulo.setText("Gestión de Inventario");
        subTitulo.setToolTipText("");
        subTitulo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        subTitulo.setFocusable(false);
        subTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel6.add(subTitulo);

        panelSuperior.add(jPanel6);

        barraLateral.add(panelSuperior, java.awt.BorderLayout.PAGE_START);

        pnlMenu.setBackground(new java.awt.Color(253, 253, 253));
        pnlMenu.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 2, new java.awt.Color(209, 213, 219)));
        pnlMenu.setFocusTraversalPolicyProvider(true);
        pnlMenu.setMinimumSize(new java.awt.Dimension(93, 600));
        pnlMenu.setLayout(new java.awt.GridLayout(0, 1, 10, 10));

        scrollMenu.setBorder(null);

        panelInterno.setBackground(new java.awt.Color(250, 249, 246));
        panelInterno.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 22, 10));
        panelInterno.setLayout(new javax.swing.BoxLayout(panelInterno, javax.swing.BoxLayout.Y_AXIS));

        btnHome.setBackground(new java.awt.Color(224, 224, 224));
        btnHome.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        btnHome.setForeground(new java.awt.Color(51, 51, 51));
        btnHome.setText("Home");
        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHome.setMaximumSize(new java.awt.Dimension(240, 60));
        btnHome.setMinimumSize(new java.awt.Dimension(240, 60));
        btnHome.setPreferredSize(new java.awt.Dimension(240, 60));
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });
        panelInterno.add(btnHome);
        panelInterno.add(filler1);

        btnTrabajadores.setBackground(new java.awt.Color(224, 224, 224));
        btnTrabajadores.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        btnTrabajadores.setForeground(new java.awt.Color(51, 51, 51));
        btnTrabajadores.setText("Trabajadores");
        btnTrabajadores.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTrabajadores.setMaximumSize(new java.awt.Dimension(240, 60));
        btnTrabajadores.setMinimumSize(new java.awt.Dimension(240, 60));
        btnTrabajadores.setPreferredSize(new java.awt.Dimension(240, 60));
        btnTrabajadores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTrabajadoresActionPerformed(evt);
            }
        });
        panelInterno.add(btnTrabajadores);
        panelInterno.add(filler2);

        btnInventario.setBackground(new java.awt.Color(224, 224, 224));
        btnInventario.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        btnInventario.setForeground(new java.awt.Color(51, 51, 51));
        btnInventario.setText("Inventario");
        btnInventario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnInventario.setMaximumSize(new java.awt.Dimension(240, 60));
        btnInventario.setMinimumSize(new java.awt.Dimension(240, 60));
        btnInventario.setPreferredSize(new java.awt.Dimension(240, 60));
        btnInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarioActionPerformed(evt);
            }
        });
        panelInterno.add(btnInventario);
        panelInterno.add(filler5);

        btnAsignaciones.setBackground(new java.awt.Color(224, 224, 224));
        btnAsignaciones.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        btnAsignaciones.setForeground(new java.awt.Color(51, 51, 51));
        btnAsignaciones.setText("Asignaciones");
        btnAsignaciones.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAsignaciones.setMaximumSize(new java.awt.Dimension(240, 60));
        btnAsignaciones.setMinimumSize(new java.awt.Dimension(240, 60));
        btnAsignaciones.setPreferredSize(new java.awt.Dimension(240, 60));
        btnAsignaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAsignacionesActionPerformed(evt);
            }
        });
        panelInterno.add(btnAsignaciones);
        panelInterno.add(filler4);

        btnSubMenuOrganizacion.setBackground(new java.awt.Color(224, 224, 224));
        btnSubMenuOrganizacion.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        btnSubMenuOrganizacion.setForeground(new java.awt.Color(51, 51, 51));
        btnSubMenuOrganizacion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSubMenuOrganizacion.setMaximumSize(new java.awt.Dimension(240, 60));
        btnSubMenuOrganizacion.setMinimumSize(new java.awt.Dimension(240, 60));
        btnSubMenuOrganizacion.setPreferredSize(new java.awt.Dimension(240, 60));
        btnSubMenuOrganizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubMenuOrganizacionActionPerformed(evt);
            }
        });
        panelInterno.add(btnSubMenuOrganizacion);
        panelInterno.add(filler3);

        subMenuConfiguracion.setBackground(new java.awt.Color(224, 224, 224));
        subMenuConfiguracion.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        subMenuConfiguracion.setForeground(new java.awt.Color(51, 51, 51));
        subMenuConfiguracion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        subMenuConfiguracion.setMaximumSize(new java.awt.Dimension(240, 60));
        subMenuConfiguracion.setMinimumSize(new java.awt.Dimension(240, 60));
        subMenuConfiguracion.setPreferredSize(new java.awt.Dimension(240, 60));
        subMenuConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuConfiguracionActionPerformed(evt);
            }
        });
        panelInterno.add(subMenuConfiguracion);

        scrollMenu.setViewportView(panelInterno);

        pnlMenu.add(scrollMenu);

        barraLateral.add(pnlMenu, java.awt.BorderLayout.CENTER);

        piePagina.setBackground(new java.awt.Color(234, 244, 251));
        piePagina.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 2, new java.awt.Color(209, 213, 219)));
        piePagina.setForeground(new java.awt.Color(0, 0, 0));
        piePagina.setPreferredSize(new java.awt.Dimension(280, 110));
        piePagina.setLayout(new javax.swing.BoxLayout(piePagina, javax.swing.BoxLayout.X_AXIS));

        jPanel2.setBackground(new java.awt.Color(250, 249, 246));
        jPanel2.setPreferredSize(new java.awt.Dimension(90, 110));
        jPanel2.setRequestFocusEnabled(false);

        miniUser.setPreferredSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(miniUser, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addComponent(miniUser, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        piePagina.add(jPanel2);

        jPanel3.setBackground(new java.awt.Color(250, 249, 246));
        jPanel3.setPreferredSize(new java.awt.Dimension(150, 110));

        tipoUser.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        tipoUser.setForeground(new java.awt.Color(51, 51, 51));
        tipoUser.setText("TipoUsuario");
        tipoUser.setMinimumSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tipoUser, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(tipoUser, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        piePagina.add(jPanel3);

        barraLateral.add(piePagina, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(barraLateral, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarioActionPerformed
        Inventario inventario = new Inventario();
        System.out.println("clic a boton inventario");
        this.panelDeContenido.removeAll();
        panelDeContenido.setLayout(new java.awt.BorderLayout());
        panelDeContenido.add(inventario.getContenido(), java.awt.BorderLayout.CENTER);
        this.panelDeContenido.revalidate();
        this.panelDeContenido.repaint();
    }//GEN-LAST:event_btnInventarioActionPerformed

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnTrabajadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTrabajadoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTrabajadoresActionPerformed

    private void btnAsignacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAsignacionesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAsignacionesActionPerformed

    private void btnSubMenuOrganizacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubMenuOrganizacionActionPerformed
        toggleMenuOrganizacion();
    }//GEN-LAST:event_btnSubMenuOrganizacionActionPerformed

    private void subMenuConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuConfiguracionActionPerformed
        toggleMenuConfiguracion();
    }//GEN-LAST:event_subMenuConfiguracionActionPerformed

    private void toggleMenuOrganizacion() {
        int indexBase = panelInterno.getComponentZOrder(btnSubMenuOrganizacion);

        if (indexBase == -1) {
            return;
        }

        if (!menuOrganizacionDesplegado) {
            this.panelInterno.add(gapOrganizacion, indexBase + 1);

            this.panelInterno.add(wrapperEmpresa, indexBase + 2);
            this.panelInterno.add(gapEmpresa, indexBase + 3);

            this.panelInterno.add(wrapperSucursal, indexBase + 4);
            this.panelInterno.add(gapSucursal, indexBase + 5);

            this.panelInterno.add(wrapperDepartamento, indexBase + 6);
            this.panelInterno.add(gapDepartamento, indexBase + 7);

            this.panelInterno.add(wrapperPuesto, indexBase + 8);

            menuOrganizacionDesplegado = true;
        } else {
            this.panelInterno.remove(wrapperEmpresa);
            this.panelInterno.remove(gapEmpresa);

            this.panelInterno.remove(wrapperSucursal);
            this.panelInterno.remove(gapSucursal);

            this.panelInterno.remove(wrapperDepartamento);
            this.panelInterno.remove(gapDepartamento);

            this.panelInterno.remove(wrapperPuesto);

            menuOrganizacionDesplegado = false;
        }
        refrescarPanel();
    }

    /**
     * Despliega o contrae el submenú de Configuración.
     */
    private void toggleMenuConfiguracion() {
        int indexBase = panelInterno.getComponentZOrder(subMenuConfiguracion);

        if (indexBase == -1) {
            return;
        }

        if (!menuConfiguracionDesplegado) {
            this.panelInterno.add(gapConfiguracion, indexBase + 1);

            this.panelInterno.add(wrapperUsuario, indexBase + 2);
            this.panelInterno.add(gapUsuario, indexBase + 3);

            this.panelInterno.add(wrapperAuditoria, indexBase + 4);

            menuConfiguracionDesplegado = true;
        } else {
            this.panelInterno.remove(wrapperUsuario);
            this.panelInterno.remove(gapUsuario);
            this.panelInterno.remove(wrapperAuditoria);

            menuConfiguracionDesplegado = false;
        }
        refrescarPanel();
    }

    /**
     * Fuerza el repintado del panel interno para mostrar cambios dinámicos.
     */
    private void refrescarPanel() {
        this.panelInterno.revalidate();
        this.panelInterno.repaint();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Métodos Helpers UI">
    /**
     * Configura un botón principal del menú (estilos y eventos).
     *
     * @param boton El JButton a configurar.
     * @param rutaIcono Ruta relativa del SVG.
     */
    private void configurarBotonMenu(JButton boton, String rutaIcono) {
        boton.setBackground(COLOR_BACKGROUND_NORMAL);
        boton.setForeground(COLOR_TEXT_NORMAL);
        boton.putClientProperty("rutaIcono", rutaIcono); // Guardamos la ruta para uso futuro

        if (rutaIcono != null) {
            boton.setIcon(crearIconoColoreado(rutaIcono, 25, 25, COLOR_TEXT_NORMAL));
        }

        boton.setBorder(new EmptyBorder(0, 10, 0, 0));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarBoton(boton);
            }
        });
    }

    /**
     * Configura un botón de submenú con dimensiones fijas.
     */
    private void configurarSubBoton(JButton btn, String ruta, Font font) {
        btn.putClientProperty("rutaIcono", ruta);
        configurarBotonMenu(btn, ruta);
        btn.setFont(font);
        btn.setMaximumSize(DIMENSION_BTN_SUBMENU);
        btn.setMinimumSize(DIMENSION_BTN_SUBMENU);
    }

    /**
     * Gestiona el cambio visual al seleccionar un botón (Active state).
     */
    private void seleccionarBoton(JButton nuevoBoton) {
        if (botonActual != null && botonActual != nuevoBoton) {
            botonActual.setBackground(COLOR_BACKGROUND_NORMAL);
            botonActual.setForeground(COLOR_TEXT_NORMAL);
            String ruta = (String) botonActual.getClientProperty("rutaIcono");
            if (ruta != null) {
                botonActual.setIcon(crearIconoColoreado(ruta, 25, 25, COLOR_TEXT_NORMAL));
            }
        }

        botonActual = nuevoBoton;
        nuevoBoton.setBackground(COLOR_SELECTED_BG);
        nuevoBoton.setForeground(COLOR_SELECTED_FG);
        String rutaNueva = (String) nuevoBoton.getClientProperty("rutaIcono");
        if (rutaNueva != null) {
            nuevoBoton.setIcon(crearIconoColoreado(rutaNueva, 25, 25, COLOR_SELECTED_FG));
        }
    }

    /**
     * Envuelve un botón en una caja horizontal con sangría izquierda.
     *
     * @param btn El botón a envolver.
     * @return Box horizontal conteniendo el espaciador y el botón.
     */
    private javax.swing.Box prepararBotonConSangria(javax.swing.JButton btn) {
        javax.swing.Box caja = javax.swing.Box.createHorizontalBox();
        caja.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        caja.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(20, 0)));

        btn.setMaximumSize(new java.awt.Dimension(Short.MAX_VALUE, 40));
        btn.setPreferredSize(new java.awt.Dimension(200, 40));
        caja.add(btn);

        return caja;
    }

    /**
     * Crea un icono SVG coloreado dinámicamente.
     */
    public FlatSVGIcon crearIconoColoreado(String ruta, int ancho, int alto, Color colorDeseado) {
        FlatSVGIcon icono = new FlatSVGIcon(ruta, ancho, alto);
        icono.setColorFilter(new FlatSVGIcon.ColorFilter(c -> colorDeseado));
        return icono;
    }

    public void setPanelDeContenido(JPanel panelDeContenido) {
        this.panelDeContenido = panelDeContenido;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Home().setVisible(true));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel barraLateral;
    private javax.swing.JButton btnAsignaciones;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnInventario;
    private javax.swing.JButton btnSubMenuOrganizacion;
    private javax.swing.JButton btnTrabajadores;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel miniUser;
    private javax.swing.JPanel panelDeContenido;
    private javax.swing.JPanel panelInterno;
    private javax.swing.JPanel panelSuperior;
    private javax.swing.JPanel piePagina;
    private javax.swing.JPanel pnlMenu;
    private javax.swing.JScrollPane scrollMenu;
    private javax.swing.JButton subMenuConfiguracion;
    private javax.swing.JLabel subTitulo;
    private javax.swing.JLabel tipoUser;
    private javax.swing.JLabel titulo;
    // End of variables declaration//GEN-END:variables
}

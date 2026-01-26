import { 
  LayoutDashboard, 
  Package, 
  UserCircle, 
  Building2, 
  Users, 
  Settings,
  ChevronDown,
  FileText
} from "lucide-react";
import { useState } from "react";
import { cn } from "@/app/components/ui/utils";

interface SidebarProps {
  currentView: string;
  onNavigate: (view: string) => void;
}

export function Sidebar({ currentView, onNavigate }: SidebarProps) {
  const [orgExpanded, setOrgExpanded] = useState(false);
  const [configExpanded, setConfigExpanded] = useState(false);

  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'inventory', label: 'Inventario', icon: Package },
    { id: 'assignments', label: 'Asignaciones', icon: UserCircle },
    { 
      id: 'organization', 
      label: 'Organización', 
      icon: Building2, 
      hasSubmenu: true,
      submenu: [
        { id: 'companies', label: 'Empresas' },
        { id: 'branches', label: 'Sucursales' },
        { id: 'departments', label: 'Departamentos' },
        { id: 'positions', label: 'Puestos' }
      ]
    },
    { id: 'users', label: 'Usuarios', icon: Users },
    { 
      id: 'config', 
      label: 'Configuración', 
      icon: Settings, 
      hasSubmenu: true,
      submenu: [
        { id: 'system-users', label: 'Usuarios del Sistema' },
        { id: 'audit', label: 'Auditoría' }
      ]
    }
  ];

  return (
    <div className="w-64 bg-card border-r border-border h-screen fixed left-0 top-0 flex flex-col">
      <div className="p-6 border-b border-border">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-primary rounded-lg">
            <Package className="w-6 h-6 text-primary-foreground" />
          </div>
          <div>
            <h1 className="font-semibold text-foreground">Sistema TI</h1>
            <p className="text-xs text-muted-foreground">Gestión de Inventario</p>
          </div>
        </div>
      </div>

      <nav className="flex-1 p-4 overflow-y-auto">
        {menuItems.map((item) => (
          <div key={item.id}>
            <button
              onClick={() => {
                if (item.id === 'organization') {
                  setOrgExpanded(!orgExpanded);
                } else if (item.id === 'config') {
                  setConfigExpanded(!configExpanded);
                } else {
                  onNavigate(item.id);
                }
              }}
              className={cn(
                "w-full flex items-center justify-between gap-3 px-3 py-2.5 rounded-lg mb-1 transition-colors",
                currentView === item.id 
                  ? "bg-primary text-primary-foreground" 
                  : "text-sidebar-foreground hover:bg-sidebar-accent"
              )}
            >
              <div className="flex items-center gap-3">
                <item.icon className="w-5 h-5" />
                <span className="text-sm">{item.label}</span>
              </div>
              {item.hasSubmenu && (
                <ChevronDown 
                  className={cn(
                    "w-4 h-4 transition-transform",
                    (item.id === 'organization' && orgExpanded) || (item.id === 'config' && configExpanded) 
                      ? "rotate-180" 
                      : ""
                  )}
                />
              )}
            </button>

            {item.hasSubmenu && item.submenu && (
              <div className={cn(
                "ml-8 space-y-1 overflow-hidden transition-all",
                (item.id === 'organization' && orgExpanded) || (item.id === 'config' && configExpanded)
                  ? "max-h-96 mb-2"
                  : "max-h-0"
              )}>
                {item.submenu.map((subItem) => (
                  <button
                    key={subItem.id}
                    onClick={() => onNavigate(subItem.id)}
                    className={cn(
                      "w-full text-left px-3 py-2 rounded-lg text-sm transition-colors",
                      currentView === subItem.id
                        ? "bg-primary/10 text-primary"
                        : "text-muted-foreground hover:bg-sidebar-accent hover:text-foreground"
                    )}
                  >
                    {subItem.label}
                  </button>
                ))}
              </div>
            )}
          </div>
        ))}
      </nav>

      <div className="p-4 border-t border-border">
        <div className="flex items-center gap-3 px-3 py-2">
          <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-primary-foreground text-sm font-semibold">
            A
          </div>
          <div className="flex-1">
            <p className="text-sm font-medium text-foreground">Admin</p>
            <p className="text-xs text-muted-foreground">admin@empresa.com</p>
          </div>
        </div>
      </div>
    </div>
  );
}

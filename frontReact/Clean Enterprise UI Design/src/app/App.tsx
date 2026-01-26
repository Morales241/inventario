import { useState } from "react";
import { Sidebar } from "@/app/components/Sidebar";
import { Dashboard } from "@/app/components/Dashboard";
import { InventoryList } from "@/app/components/InventoryList";
import { EquipmentForm } from "@/app/components/EquipmentForm";
import { AssignmentForm } from "@/app/components/AssignmentForm";
import { PlaceholderView } from "@/app/components/PlaceholderView";

export default function App() {
  const [currentView, setCurrentView] = useState('dashboard');

  const renderView = () => {
    switch (currentView) {
      case 'dashboard':
        return <Dashboard />;
      
      case 'inventory':
        return <InventoryList onNavigateToForm={() => setCurrentView('equipment-form')} />;
      
      case 'equipment-form':
        return <EquipmentForm onBack={() => setCurrentView('inventory')} />;
      
      case 'assignments':
        return <AssignmentForm />;
      
      case 'companies':
        return <PlaceholderView title="Empresas" description="Gestión de empresas del grupo" />;
      
      case 'branches':
        return <PlaceholderView title="Sucursales" description="Gestión de sucursales y ubicaciones" />;
      
      case 'departments':
        return <PlaceholderView title="Departamentos" description="Gestión de departamentos organizacionales" />;
      
      case 'positions':
        return <PlaceholderView title="Puestos" description="Gestión de puestos y roles" />;
      
      case 'users':
        return <PlaceholderView title="Usuarios" description="Gestión de empleados y trabajadores" />;
      
      case 'system-users':
        return <PlaceholderView title="Usuarios del Sistema" description="Gestión de usuarios administradores" />;
      
      case 'audit':
        return <PlaceholderView title="Auditoría" description="Registro completo de movimientos y cambios" />;
      
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="flex h-screen bg-background">
      <Sidebar currentView={currentView} onNavigate={setCurrentView} />
      
      <main className="flex-1 ml-64 overflow-auto">
        <div className="p-8 max-w-[1600px] mx-auto">
          {renderView()}
        </div>
      </main>
    </div>
  );
}

import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/app/components/ui/dialog";
import { Input } from "@/app/components/ui/input";
import { Button } from "@/app/components/ui/button";
import { Label } from "@/app/components/ui/label";
import { Shield, Wrench, UserCircle, Check } from "lucide-react";
import { cn } from "@/app/components/ui/utils";

interface UserModalProps {
  open: boolean;
  onClose: () => void;
  onSave: (userData: any) => void;
}

const roles = [
  {
    id: 'admin',
    name: 'Administrador',
    description: 'Acceso completo al sistema, puede crear usuarios y configurar todo',
    icon: Shield,
    color: 'bg-red-50 border-red-200 hover:bg-red-100',
    activeColor: 'bg-red-100 border-red-500',
    iconColor: 'text-red-600',
    badgeColor: 'bg-red-100 text-red-700'
  },
  {
    id: 'technician',
    name: 'Técnico',
    description: 'Puede gestionar inventario, asignaciones y ver reportes',
    icon: Wrench,
    color: 'bg-blue-50 border-blue-200 hover:bg-blue-100',
    activeColor: 'bg-blue-100 border-blue-500',
    iconColor: 'text-blue-600',
    badgeColor: 'bg-blue-100 text-blue-700'
  },
  {
    id: 'operator',
    name: 'Operario',
    description: 'Solo puede ver inventario y registrar movimientos básicos',
    icon: UserCircle,
    color: 'bg-green-50 border-green-200 hover:bg-green-100',
    activeColor: 'bg-green-100 border-green-500',
    iconColor: 'text-green-600',
    badgeColor: 'bg-green-100 text-green-700'
  }
];

export function UserModal({ open, onClose, onSave }: UserModalProps) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [selectedRole, setSelectedRole] = useState<string>('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (password !== confirmPassword) {
      alert('Las contraseñas no coinciden');
      return;
    }

    onSave({
      name,
      email,
      username,
      password,
      role: selectedRole
    });

    // Reset form
    setName('');
    setEmail('');
    setUsername('');
    setPassword('');
    setConfirmPassword('');
    setSelectedRole('');
    onClose();
  };

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Crear Nuevo Usuario del Sistema</DialogTitle>
          <DialogDescription>
            Completa la información para crear un nuevo usuario administrador
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6 pt-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="name">Nombre Completo *</Label>
              <Input
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Juan Pérez García"
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="email">Correo Electrónico *</Label>
              <Input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="juan.perez@empresa.com"
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="username">Nombre de Usuario *</Label>
              <Input
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="jperez"
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">Contraseña *</Label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                required
              />
            </div>

            <div className="space-y-2 col-span-2">
              <Label htmlFor="confirmPassword">Confirmar Contraseña *</Label>
              <Input
                id="confirmPassword"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="••••••••"
                required
              />
            </div>
          </div>

          <div className="space-y-3">
            <Label>Seleccionar Rol *</Label>
            <div className="grid grid-cols-1 gap-3">
              {roles.map((role) => {
                const Icon = role.icon;
                const isSelected = selectedRole === role.id;
                
                return (
                  <div
                    key={role.id}
                    onClick={() => setSelectedRole(role.id)}
                    className={cn(
                      "relative border-2 rounded-lg p-4 cursor-pointer transition-all",
                      isSelected ? role.activeColor : role.color
                    )}
                  >
                    <div className="flex items-start gap-4">
                      <div className={cn(
                        "w-12 h-12 rounded-lg flex items-center justify-center",
                        isSelected ? 'bg-white shadow-sm' : 'bg-white/50'
                      )}>
                        <Icon className={cn("w-6 h-6", role.iconColor)} />
                      </div>
                      
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <h4 className="font-semibold">{role.name}</h4>
                          {isSelected && (
                            <div className="ml-auto">
                              <Check className={cn("w-5 h-5", role.iconColor)} />
                            </div>
                          )}
                        </div>
                        <p className="text-sm text-muted-foreground">
                          {role.description}
                        </p>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <div className="flex items-center gap-3 justify-end pt-4 border-t">
            <Button type="button" variant="outline" onClick={onClose}>
              Cancelar
            </Button>
            <Button 
              type="submit" 
              className="bg-primary hover:bg-primary/90"
              disabled={!selectedRole || !name || !email || !username || !password || !confirmPassword}
            >
              Crear Usuario
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}

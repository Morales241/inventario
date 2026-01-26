import { useState } from "react";
import { Card } from "@/app/components/ui/card";
import { Input } from "@/app/components/ui/input";
import { Button } from "@/app/components/ui/button";
import { Badge } from "@/app/components/ui/badge";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/app/components/ui/table";
import { Avatar, AvatarFallback } from "@/app/components/ui/avatar";
import { UserModal } from "@/app/components/UserModal";
import { Plus, Search, Edit, Trash2, Shield, Wrench, UserCircle } from "lucide-react";

interface SystemUser {
  id: number;
  name: string;
  email: string;
  username: string;
  role: 'admin' | 'technician' | 'operator';
  status: 'active' | 'inactive';
  lastLogin: string;
}

const mockUsers: SystemUser[] = [
  {
    id: 1,
    name: 'Carlos Administrador',
    email: 'admin@empresa.com',
    username: 'cadmin',
    role: 'admin',
    status: 'active',
    lastLogin: '2026-01-21 10:30'
  },
  {
    id: 2,
    name: 'María Técnico',
    email: 'mtecnico@empresa.com',
    username: 'mtecnico',
    role: 'technician',
    status: 'active',
    lastLogin: '2026-01-21 09:15'
  },
  {
    id: 3,
    name: 'Juan Operador',
    email: 'joperador@empresa.com',
    username: 'joperador',
    role: 'operator',
    status: 'active',
    lastLogin: '2026-01-20 16:45'
  },
  {
    id: 4,
    name: 'Ana Soporte',
    email: 'asoporte@empresa.com',
    username: 'asoporte',
    role: 'technician',
    status: 'active',
    lastLogin: '2026-01-21 08:20'
  },
  {
    id: 5,
    name: 'Roberto Almacén',
    email: 'ralmacen@empresa.com',
    username: 'ralmacen',
    role: 'operator',
    status: 'inactive',
    lastLogin: '2026-01-15 14:30'
  },
  {
    id: 6,
    name: 'Laura Admin',
    email: 'ladmin@empresa.com',
    username: 'ladmin',
    role: 'admin',
    status: 'active',
    lastLogin: '2026-01-21 07:50'
  }
];

export function SystemUsers() {
  const [users, setUsers] = useState<SystemUser[]>(mockUsers);
  const [searchTerm, setSearchTerm] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [roleFilter, setRoleFilter] = useState<string>('all');
  const [statusFilter, setStatusFilter] = useState<string>('all');

  const getRoleBadge = (role: string) => {
    switch (role) {
      case 'admin':
        return {
          label: 'Admin',
          className: 'bg-red-100 text-red-700 border-red-200',
          icon: Shield
        };
      case 'technician':
        return {
          label: 'Técnico',
          className: 'bg-blue-100 text-blue-700 border-blue-200',
          icon: Wrench
        };
      case 'operator':
        return {
          label: 'Operario',
          className: 'bg-green-100 text-green-700 border-green-200',
          icon: UserCircle
        };
      default:
        return {
          label: 'Usuario',
          className: 'bg-gray-100 text-gray-700 border-gray-200',
          icon: UserCircle
        };
    }
  };

  const getStatusBadge = (status: string) => {
    return status === 'active'
      ? 'bg-green-100 text-green-700 border-green-200'
      : 'bg-gray-100 text-gray-700 border-gray-200';
  };

  const getInitials = (name: string) => {
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  };

  const handleCreateUser = (userData: any) => {
    const newUser: SystemUser = {
      id: users.length + 1,
      name: userData.name,
      email: userData.email,
      username: userData.username,
      role: userData.role,
      status: 'active',
      lastLogin: 'Nunca'
    };
    setUsers([...users, newUser]);
  };

  const filteredUsers = users.filter(user => {
    const matchesSearch = user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         user.username.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesRole = roleFilter === 'all' || user.role === roleFilter;
    const matchesStatus = statusFilter === 'all' || user.status === statusFilter;
    
    return matchesSearch && matchesRole && matchesStatus;
  });

  const roleStats = {
    admin: users.filter(u => u.role === 'admin').length,
    technician: users.filter(u => u.role === 'technician').length,
    operator: users.filter(u => u.role === 'operator').length,
    active: users.filter(u => u.status === 'active').length
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-foreground mb-1">Usuarios del Sistema</h1>
          <p className="text-sm text-muted-foreground">Gestión de administradores, técnicos y operarios</p>
        </div>
        <Button onClick={() => setIsModalOpen(true)} className="bg-primary hover:bg-primary/90 gap-2">
          <Plus className="w-4 h-4" />
          Crear Nuevo Usuario
        </Button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground mb-1">Administradores</p>
              <p className="text-2xl font-semibold">{roleStats.admin}</p>
            </div>
            <div className="w-10 h-10 rounded-lg bg-red-100 flex items-center justify-center">
              <Shield className="w-5 h-5 text-red-600" />
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground mb-1">Técnicos</p>
              <p className="text-2xl font-semibold">{roleStats.technician}</p>
            </div>
            <div className="w-10 h-10 rounded-lg bg-blue-100 flex items-center justify-center">
              <Wrench className="w-5 h-5 text-blue-600" />
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground mb-1">Operarios</p>
              <p className="text-2xl font-semibold">{roleStats.operator}</p>
            </div>
            <div className="w-10 h-10 rounded-lg bg-green-100 flex items-center justify-center">
              <UserCircle className="w-5 h-5 text-green-600" />
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground mb-1">Usuarios Activos</p>
              <p className="text-2xl font-semibold">{roleStats.active}</p>
            </div>
            <div className="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center">
              <UserCircle className="w-5 h-5 text-primary" />
            </div>
          </div>
        </Card>
      </div>

      {/* Filters */}
      <Card className="p-6">
        <div className="flex flex-wrap gap-4">
          <div className="flex-1 min-w-[200px] relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Buscar por nombre, email o usuario"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-9"
            />
          </div>
          
          <div className="flex gap-2">
            <Button
              variant={roleFilter === 'all' ? 'default' : 'outline'}
              onClick={() => setRoleFilter('all')}
              size="sm"
            >
              Todos
            </Button>
            <Button
              variant={roleFilter === 'admin' ? 'default' : 'outline'}
              onClick={() => setRoleFilter('admin')}
              size="sm"
              className={roleFilter === 'admin' ? 'bg-red-600 hover:bg-red-700' : ''}
            >
              Admins
            </Button>
            <Button
              variant={roleFilter === 'technician' ? 'default' : 'outline'}
              onClick={() => setRoleFilter('technician')}
              size="sm"
              className={roleFilter === 'technician' ? 'bg-blue-600 hover:bg-blue-700' : ''}
            >
              Técnicos
            </Button>
            <Button
              variant={roleFilter === 'operator' ? 'default' : 'outline'}
              onClick={() => setRoleFilter('operator')}
              size="sm"
              className={roleFilter === 'operator' ? 'bg-green-600 hover:bg-green-700' : ''}
            >
              Operarios
            </Button>
          </div>
        </div>
      </Card>

      {/* Users Table */}
      <Card className="p-6">
        <div className="mb-4">
          <p className="text-sm text-muted-foreground">
            Mostrando {filteredUsers.length} de {users.length} usuarios
          </p>
        </div>

        <div className="border rounded-lg">
          <Table>
            <TableHeader>
              <TableRow className="bg-muted/50">
                <TableHead className="font-semibold">Usuario</TableHead>
                <TableHead className="font-semibold">Correo Electrónico</TableHead>
                <TableHead className="font-semibold">Nombre de Usuario</TableHead>
                <TableHead className="font-semibold">Rol</TableHead>
                <TableHead className="font-semibold">Estado</TableHead>
                <TableHead className="font-semibold">Último Acceso</TableHead>
                <TableHead className="font-semibold text-right">Acciones</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredUsers.map((user) => {
                const roleBadge = getRoleBadge(user.role);
                const RoleIcon = roleBadge.icon;
                
                return (
                  <TableRow key={user.id} className="hover:bg-muted/30">
                    <TableCell>
                      <div className="flex items-center gap-3">
                        <Avatar className="w-10 h-10">
                          <AvatarFallback className="bg-primary text-primary-foreground font-semibold">
                            {getInitials(user.name)}
                          </AvatarFallback>
                        </Avatar>
                        <span className="font-medium">{user.name}</span>
                      </div>
                    </TableCell>
                    <TableCell className="text-sm">{user.email}</TableCell>
                    <TableCell className="text-sm font-mono">{user.username}</TableCell>
                    <TableCell>
                      <Badge variant="outline" className={`${roleBadge.className} gap-1.5`}>
                        <RoleIcon className="w-3.5 h-3.5" />
                        {roleBadge.label}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <Badge variant="outline" className={getStatusBadge(user.status)}>
                        {user.status === 'active' ? 'Activo' : 'Inactivo'}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-sm text-muted-foreground">
                      {user.lastLogin}
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center justify-end gap-2">
                        <Button size="sm" variant="ghost" className="h-8 w-8 p-0">
                          <Edit className="w-4 h-4" />
                        </Button>
                        <Button size="sm" variant="ghost" className="h-8 w-8 p-0 text-destructive">
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </div>
      </Card>

      <UserModal 
        open={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleCreateUser}
      />
    </div>
  );
}

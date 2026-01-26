import { useState } from "react";
import { Card } from "@/app/components/ui/card";
import { Input } from "@/app/components/ui/input";
import { Button } from "@/app/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/app/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/app/components/ui/table";
import { Badge } from "@/app/components/ui/badge";
import { Search, Calendar, Download, Filter } from "lucide-react";

interface AuditEntry {
  id: number;
  timestamp: string;
  user: string;
  action: 'create' | 'edit' | 'delete' | 'assign' | 'return';
  entity: string;
  entityId: string;
  details: string;
  oldValue?: string;
  newValue?: string;
  ipAddress?: string;
}

const mockAuditData: AuditEntry[] = [
  {
    id: 1,
    timestamp: '2026-01-21 10:35:24',
    user: 'Carlos Admin',
    action: 'create',
    entity: 'Equipo',
    entityId: 'LAP-HP-2341',
    details: 'Nuevo equipo HP ProBook 450 G8 registrado en el sistema',
    ipAddress: '192.168.1.105'
  },
  {
    id: 2,
    timestamp: '2026-01-21 10:22:15',
    user: 'María Técnico',
    action: 'assign',
    entity: 'Asignación',
    entityId: 'ASG-0234',
    details: 'Equipo iPhone 13 asignado a Juan Pérez del departamento de Marketing',
    ipAddress: '192.168.1.112'
  },
  {
    id: 3,
    timestamp: '2026-01-21 09:48:03',
    user: 'Ana Soporte',
    action: 'edit',
    entity: 'Equipo',
    entityId: 'DSK-DEL-9012',
    details: 'Actualización de estado del equipo',
    oldValue: 'Nuevo',
    newValue: 'En Reparación',
    ipAddress: '192.168.1.108'
  },
  {
    id: 4,
    timestamp: '2026-01-21 09:15:42',
    user: 'Roberto Almacén',
    action: 'return',
    entity: 'Asignación',
    entityId: 'ASG-0198',
    details: 'Devolución de MacBook Air M2 a almacén',
    ipAddress: '192.168.1.120'
  },
  {
    id: 5,
    timestamp: '2026-01-21 08:52:11',
    user: 'Carlos Admin',
    action: 'delete',
    entity: 'Usuario',
    entityId: 'USR-045',
    details: 'Usuario "Pedro Prueba" eliminado del sistema',
    oldValue: 'pedro.prueba@empresa.com',
    ipAddress: '192.168.1.105'
  },
  {
    id: 6,
    timestamp: '2026-01-20 17:30:55',
    user: 'María Técnico',
    action: 'create',
    entity: 'Sucursal',
    entityId: 'SUC-004',
    details: 'Nueva sucursal "Sucursal Sur" creada en Puebla',
    ipAddress: '192.168.1.112'
  },
  {
    id: 7,
    timestamp: '2026-01-20 16:45:22',
    user: 'Ana Soporte',
    action: 'edit',
    entity: 'Trabajador',
    entityId: 'EMP-0156',
    details: 'Actualización de información de empleado',
    oldValue: 'Desarrollador Junior',
    newValue: 'Desarrollador Senior',
    ipAddress: '192.168.1.108'
  },
  {
    id: 8,
    timestamp: '2026-01-20 15:20:08',
    user: 'Juan Operador',
    action: 'assign',
    entity: 'Asignación',
    entityId: 'ASG-0233',
    details: 'Tablet iPad Pro asignada a Laura Torres',
    ipAddress: '192.168.1.145'
  },
  {
    id: 9,
    timestamp: '2026-01-20 14:10:33',
    user: 'Carlos Admin',
    action: 'edit',
    entity: 'Configuración',
    entityId: 'CFG-SYS',
    details: 'Actualización de configuración del sistema',
    oldValue: 'auto_backup: false',
    newValue: 'auto_backup: true',
    ipAddress: '192.168.1.105'
  },
  {
    id: 10,
    timestamp: '2026-01-20 13:25:17',
    user: 'María Técnico',
    action: 'create',
    entity: 'Departamento',
    entityId: 'DEPT-012',
    details: 'Nuevo departamento "Innovación Digital" creado',
    ipAddress: '192.168.1.112'
  },
  {
    id: 11,
    timestamp: '2026-01-20 11:40:50',
    user: 'Ana Soporte',
    action: 'delete',
    entity: 'Equipo',
    entityId: 'LAP-OLD-8890',
    details: 'Equipo dado de baja por obsolescencia',
    oldValue: 'Estado: Baja',
    ipAddress: '192.168.1.108'
  },
  {
    id: 12,
    timestamp: '2026-01-20 10:15:42',
    user: 'Roberto Almacén',
    action: 'return',
    entity: 'Asignación',
    entityId: 'ASG-0189',
    details: 'Devolución de Desktop Dell OptiPlex',
    ipAddress: '192.168.1.120'
  },
  {
    id: 13,
    timestamp: '2026-01-19 16:55:28',
    user: 'Carlos Admin',
    action: 'create',
    entity: 'Usuario',
    entityId: 'USR-046',
    details: 'Nuevo usuario "Laura Admin" creado con rol Administrador',
    ipAddress: '192.168.1.105'
  },
  {
    id: 14,
    timestamp: '2026-01-19 15:30:12',
    user: 'María Técnico',
    action: 'edit',
    entity: 'Equipo',
    entityId: 'MON-SAM-3321',
    details: 'Actualización de información de móvil Samsung',
    oldValue: 'IMEI: 352847098765432',
    newValue: 'IMEI: 352847098765433',
    ipAddress: '192.168.1.112'
  },
  {
    id: 15,
    timestamp: '2026-01-19 14:20:05',
    user: 'Juan Operador',
    action: 'assign',
    entity: 'Asignación',
    entityId: 'ASG-0232',
    details: 'Laptop Lenovo ThinkPad asignada a Carlos López',
    ipAddress: '192.168.1.145'
  }
];

export function AuditLog() {
  const [searchTerm, setSearchTerm] = useState('');
  const [userFilter, setUserFilter] = useState('all');
  const [actionFilter, setActionFilter] = useState('all');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const getActionBadge = (action: string) => {
    switch (action) {
      case 'create':
        return {
          label: 'Creación',
          className: 'bg-green-50 text-green-700 border-green-200'
        };
      case 'edit':
        return {
          label: 'Edición',
          className: 'bg-orange-50 text-orange-700 border-orange-200'
        };
      case 'delete':
        return {
          label: 'Eliminación',
          className: 'bg-red-50 text-red-700 border-red-200'
        };
      case 'assign':
        return {
          label: 'Asignación',
          className: 'bg-blue-50 text-blue-700 border-blue-200'
        };
      case 'return':
        return {
          label: 'Devolución',
          className: 'bg-purple-50 text-purple-700 border-purple-200'
        };
      default:
        return {
          label: 'Acción',
          className: 'bg-gray-50 text-gray-700 border-gray-200'
        };
    }
  };

  const filteredLogs = mockAuditData.filter(log => {
    const matchesSearch = log.details.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         log.entityId.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         log.user.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesUser = userFilter === 'all' || log.user === userFilter;
    const matchesAction = actionFilter === 'all' || log.action === actionFilter;
    
    // Date filtering
    let matchesDate = true;
    if (startDate) {
      matchesDate = matchesDate && log.timestamp >= startDate;
    }
    if (endDate) {
      matchesDate = matchesDate && log.timestamp <= endDate + ' 23:59:59';
    }
    
    return matchesSearch && matchesUser && matchesAction && matchesDate;
  });

  const uniqueUsers = Array.from(new Set(mockAuditData.map(log => log.user)));

  const actionStats = {
    create: mockAuditData.filter(l => l.action === 'create').length,
    edit: mockAuditData.filter(l => l.action === 'edit').length,
    delete: mockAuditData.filter(l => l.action === 'delete').length,
    assign: mockAuditData.filter(l => l.action === 'assign').length
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-foreground mb-1">Auditoría del Sistema</h1>
          <p className="text-sm text-muted-foreground">Registro completo de movimientos y cambios</p>
        </div>
        <Button className="bg-primary hover:bg-primary/90 gap-2">
          <Download className="w-4 h-4" />
          Exportar Reporte
        </Button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="p-4 border-l-4 border-l-green-500">
          <p className="text-sm text-muted-foreground mb-1">Creaciones</p>
          <p className="text-2xl font-semibold">{actionStats.create}</p>
        </Card>
        <Card className="p-4 border-l-4 border-l-orange-500">
          <p className="text-sm text-muted-foreground mb-1">Ediciones</p>
          <p className="text-2xl font-semibold">{actionStats.edit}</p>
        </Card>
        <Card className="p-4 border-l-4 border-l-red-500">
          <p className="text-sm text-muted-foreground mb-1">Eliminaciones</p>
          <p className="text-2xl font-semibold">{actionStats.delete}</p>
        </Card>
        <Card className="p-4 border-l-4 border-l-blue-500">
          <p className="text-sm text-muted-foreground mb-1">Asignaciones</p>
          <p className="text-2xl font-semibold">{actionStats.assign}</p>
        </Card>
      </div>

      {/* Filters */}
      <Card className="p-6">
        <div className="flex items-center gap-2 mb-4">
          <Filter className="w-4 h-4 text-muted-foreground" />
          <h3 className="font-semibold">Filtros de Búsqueda</h3>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
          <div className="lg:col-span-2 relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Buscar en logs..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-9"
            />
          </div>

          <div className="relative">
            <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground z-10 pointer-events-none" />
            <Input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              placeholder="Fecha Inicio"
              className="pl-9"
            />
          </div>

          <div className="relative">
            <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground z-10 pointer-events-none" />
            <Input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              placeholder="Fecha Fin"
              className="pl-9"
            />
          </div>

          <Select value={userFilter} onValueChange={setUserFilter}>
            <SelectTrigger>
              <SelectValue placeholder="Usuario" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Todos los Usuarios</SelectItem>
              {uniqueUsers.map(user => (
                <SelectItem key={user} value={user}>{user}</SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <div className="flex gap-2 mt-4">
          <Button
            variant={actionFilter === 'all' ? 'default' : 'outline'}
            onClick={() => setActionFilter('all')}
            size="sm"
          >
            Todas
          </Button>
          <Button
            variant={actionFilter === 'create' ? 'default' : 'outline'}
            onClick={() => setActionFilter('create')}
            size="sm"
            className={actionFilter === 'create' ? 'bg-green-600 hover:bg-green-700' : ''}
          >
            Creaciones
          </Button>
          <Button
            variant={actionFilter === 'edit' ? 'default' : 'outline'}
            onClick={() => setActionFilter('edit')}
            size="sm"
            className={actionFilter === 'edit' ? 'bg-orange-600 hover:bg-orange-700' : ''}
          >
            Ediciones
          </Button>
          <Button
            variant={actionFilter === 'delete' ? 'default' : 'outline'}
            onClick={() => setActionFilter('delete')}
            size="sm"
            className={actionFilter === 'delete' ? 'bg-red-600 hover:bg-red-700' : ''}
          >
            Eliminaciones
          </Button>
          <Button
            variant={actionFilter === 'assign' ? 'default' : 'outline'}
            onClick={() => setActionFilter('assign')}
            size="sm"
            className={actionFilter === 'assign' ? 'bg-blue-600 hover:bg-blue-700' : ''}
          >
            Asignaciones
          </Button>
        </div>
      </Card>

      {/* Audit Log Table */}
      <Card className="p-6">
        <div className="mb-4">
          <p className="text-sm text-muted-foreground">
            Mostrando {filteredLogs.length} de {mockAuditData.length} registros
          </p>
        </div>

        <div className="border rounded-lg overflow-hidden">
          <Table>
            <TableHeader>
              <TableRow className="bg-muted/50">
                <TableHead className="font-semibold">Fecha/Hora</TableHead>
                <TableHead className="font-semibold">Usuario</TableHead>
                <TableHead className="font-semibold">Acción</TableHead>
                <TableHead className="font-semibold">Entidad</TableHead>
                <TableHead className="font-semibold">ID</TableHead>
                <TableHead className="font-semibold">Detalles</TableHead>
                <TableHead className="font-semibold">Cambios</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredLogs.map((log) => {
                const actionBadge = getActionBadge(log.action);
                
                return (
                  <TableRow key={log.id} className="hover:bg-muted/30">
                    <TableCell className="text-sm font-mono whitespace-nowrap">
                      {log.timestamp}
                    </TableCell>
                    <TableCell className="text-sm font-medium">
                      {log.user}
                    </TableCell>
                    <TableCell>
                      <Badge variant="outline" className={actionBadge.className}>
                        {actionBadge.label}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-sm">
                      {log.entity}
                    </TableCell>
                    <TableCell>
                      <code className="text-xs font-mono bg-muted px-2 py-1 rounded">
                        {log.entityId}
                      </code>
                    </TableCell>
                    <TableCell className="text-sm max-w-md">
                      {log.details}
                    </TableCell>
                    <TableCell className="text-xs">
                      {log.oldValue && log.newValue && (
                        <div className="space-y-1 min-w-[200px]">
                          <div className="font-mono bg-red-50 text-red-700 px-2 py-1 rounded border border-red-200">
                            <span className="text-red-500">-</span> {log.oldValue}
                          </div>
                          <div className="font-mono bg-green-50 text-green-700 px-2 py-1 rounded border border-green-200">
                            <span className="text-green-500">+</span> {log.newValue}
                          </div>
                        </div>
                      )}
                      {log.oldValue && !log.newValue && (
                        <div className="font-mono bg-red-50 text-red-700 px-2 py-1 rounded border border-red-200 text-xs">
                          {log.oldValue}
                        </div>
                      )}
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </div>
      </Card>
    </div>
  );
}

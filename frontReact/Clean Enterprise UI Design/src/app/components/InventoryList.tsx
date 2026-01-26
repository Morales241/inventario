import { useState } from "react";
import { Card } from "@/app/components/ui/card";
import { Input } from "@/app/components/ui/input";
import { Button } from "@/app/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/app/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/app/components/ui/table";
import { Plus, Edit, UserPlus, Trash2, Search } from "lucide-react";
import { Badge } from "@/app/components/ui/badge";

interface InventoryListProps {
  onNavigateToForm: () => void;
}

const mockInventory = [
  { id: 1, serial: 'LAP-HP-2341', brand: 'HP', model: 'ProBook 450 G8', type: 'Laptop', status: 'Nuevo', branch: 'Matriz', assignedTo: 'Juan Pérez' },
  { id: 2, serial: 'IPH-13-7823', brand: 'Apple', model: 'iPhone 13', type: 'Móvil', status: 'Usado', branch: 'Sucursal A', assignedTo: '-' },
  { id: 3, serial: 'DSK-DEL-9012', brand: 'Dell', model: 'OptiPlex 7090', type: 'Escritorio', status: 'Nuevo', branch: 'Matriz', assignedTo: 'María García' },
  { id: 4, serial: 'IPD-PRO-4456', brand: 'Apple', model: 'iPad Pro 11"', type: 'Tablet', status: 'Nuevo', branch: 'Sucursal B', assignedTo: '-' },
  { id: 5, serial: 'MAC-AIR-8890', brand: 'Apple', model: 'MacBook Air M2', type: 'Laptop', status: 'Usado', branch: 'Sucursal A', assignedTo: 'Carlos López' },
  { id: 6, serial: 'MON-SAM-3321', brand: 'Samsung', model: 'Galaxy S23', type: 'Móvil', status: 'Nuevo', branch: 'Matriz', assignedTo: 'Ana Martínez' },
  { id: 7, serial: 'LAP-LEN-5567', brand: 'Lenovo', model: 'ThinkPad X1', type: 'Laptop', status: 'Usado', branch: 'Sucursal C', assignedTo: '-' },
  { id: 8, serial: 'DSK-HP-7788', brand: 'HP', model: 'EliteDesk 800', type: 'Escritorio', status: 'Baja', branch: 'Matriz', assignedTo: '-' },
  { id: 9, serial: 'TAB-SAM-9901', brand: 'Samsung', model: 'Galaxy Tab S8', type: 'Tablet', status: 'Nuevo', branch: 'Sucursal B', assignedTo: 'Roberto Silva' },
  { id: 10, serial: 'LAP-ASU-1122', brand: 'Asus', model: 'ZenBook 14', type: 'Laptop', status: 'Usado', branch: 'Sucursal A', assignedTo: 'Laura Torres' },
];

export function InventoryList({ onNavigateToForm }: InventoryListProps) {
  const [serialFilter, setSerialFilter] = useState('');
  const [typeFilter, setTypeFilter] = useState('all');
  const [statusFilter, setStatusFilter] = useState('all');
  const [branchFilter, setBranchFilter] = useState('all');

  const filteredInventory = mockInventory.filter(item => {
    const matchesSerial = item.serial.toLowerCase().includes(serialFilter.toLowerCase()) ||
                          item.model.toLowerCase().includes(serialFilter.toLowerCase());
    const matchesType = typeFilter === 'all' || item.type === typeFilter;
    const matchesStatus = statusFilter === 'all' || item.status === statusFilter;
    const matchesBranch = branchFilter === 'all' || item.branch === branchFilter;
    
    return matchesSerial && matchesType && matchesStatus && matchesBranch;
  });

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Nuevo': return 'bg-green-100 text-green-700 border-green-200';
      case 'Usado': return 'bg-blue-100 text-blue-700 border-blue-200';
      case 'Baja': return 'bg-red-100 text-red-700 border-red-200';
      default: return 'bg-gray-100 text-gray-700 border-gray-200';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-foreground mb-1">Inventario</h1>
          <p className="text-sm text-muted-foreground">Gestión de equipos de TI</p>
        </div>
        <Button onClick={onNavigateToForm} className="bg-primary hover:bg-primary/90 gap-2">
          <Plus className="w-4 h-4" />
          Agregar Nuevo Equipo
        </Button>
      </div>

      {/* Advanced Filters */}
      <Card className="p-6">
        <h3 className="text-sm font-semibold mb-4">Filtros Avanzados</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Buscar por No. Serie o Modelo"
              value={serialFilter}
              onChange={(e) => setSerialFilter(e.target.value)}
              className="pl-9"
            />
          </div>
          
          <Select value={typeFilter} onValueChange={setTypeFilter}>
            <SelectTrigger>
              <SelectValue placeholder="Tipo de Equipo" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Todos los Tipos</SelectItem>
              <SelectItem value="Móvil">Móvil</SelectItem>
              <SelectItem value="Laptop">Laptop</SelectItem>
              <SelectItem value="Escritorio">Escritorio</SelectItem>
              <SelectItem value="Tablet">Tablet</SelectItem>
            </SelectContent>
          </Select>

          <Select value={statusFilter} onValueChange={setStatusFilter}>
            <SelectTrigger>
              <SelectValue placeholder="Estado" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Todos los Estados</SelectItem>
              <SelectItem value="Nuevo">Nuevo</SelectItem>
              <SelectItem value="Usado">Usado</SelectItem>
              <SelectItem value="Baja">Baja</SelectItem>
            </SelectContent>
          </Select>

          <Select value={branchFilter} onValueChange={setBranchFilter}>
            <SelectTrigger>
              <SelectValue placeholder="Sucursal" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Todas las Sucursales</SelectItem>
              <SelectItem value="Matriz">Matriz</SelectItem>
              <SelectItem value="Sucursal A">Sucursal A</SelectItem>
              <SelectItem value="Sucursal B">Sucursal B</SelectItem>
              <SelectItem value="Sucursal C">Sucursal C</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </Card>

      {/* Data Table */}
      <Card className="p-6">
        <div className="mb-4 flex items-center justify-between">
          <p className="text-sm text-muted-foreground">
            Mostrando {filteredInventory.length} de {mockInventory.length} equipos
          </p>
        </div>
        
        <div className="border rounded-lg">
          <Table>
            <TableHeader>
              <TableRow className="bg-muted/50">
                <TableHead className="font-semibold">ID</TableHead>
                <TableHead className="font-semibold">No. Serie</TableHead>
                <TableHead className="font-semibold">Marca</TableHead>
                <TableHead className="font-semibold">Modelo</TableHead>
                <TableHead className="font-semibold">Tipo</TableHead>
                <TableHead className="font-semibold">Estado</TableHead>
                <TableHead className="font-semibold">Sucursal</TableHead>
                <TableHead className="font-semibold">Asignado a</TableHead>
                <TableHead className="font-semibold text-right">Acciones</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredInventory.map((item) => (
                <TableRow key={item.id} className="hover:bg-muted/30">
                  <TableCell className="text-sm font-medium">#{item.id}</TableCell>
                  <TableCell className="text-sm font-mono">{item.serial}</TableCell>
                  <TableCell className="text-sm">{item.brand}</TableCell>
                  <TableCell className="text-sm">{item.model}</TableCell>
                  <TableCell className="text-sm">{item.type}</TableCell>
                  <TableCell>
                    <Badge variant="outline" className={getStatusColor(item.status)}>
                      {item.status}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-sm">{item.branch}</TableCell>
                  <TableCell className="text-sm">
                    {item.assignedTo === '-' ? (
                      <span className="text-muted-foreground italic">Sin asignar</span>
                    ) : (
                      <span className="font-medium">{item.assignedTo}</span>
                    )}
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center justify-end gap-2">
                      <Button size="sm" variant="ghost" className="h-8 w-8 p-0">
                        <Edit className="w-4 h-4" />
                      </Button>
                      <Button size="sm" variant="ghost" className="h-8 w-8 p-0">
                        <UserPlus className="w-4 h-4" />
                      </Button>
                      <Button size="sm" variant="ghost" className="h-8 w-8 p-0 text-destructive">
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      </Card>
    </div>
  );
}

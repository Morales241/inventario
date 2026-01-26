import { KPICard } from "@/app/components/KPICard";
import { Card } from "@/app/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/app/components/ui/table";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { Package, CheckCircle, UserCircle, Wrench } from "lucide-react";

const equipmentByBranch = [
  { name: 'Matriz', equipos: 45 },
  { name: 'Sucursal A', equipos: 28 },
  { name: 'Sucursal B', equipos: 32 },
  { name: 'Sucursal C', equipos: 19 },
  { name: 'Sucursal D', equipos: 23 }
];

const recentMovements = [
  { id: 1, date: '2026-01-21 09:30', user: 'Juan Pérez', action: 'Asignación', equipment: 'Laptop HP-2341', details: 'Asignado a Marketing' },
  { id: 2, date: '2026-01-21 08:15', user: 'María García', action: 'Devolución', equipment: 'iPhone 13-7823', details: 'Devuelto a almacén' },
  { id: 3, date: '2026-01-20 16:45', user: 'Carlos López', action: 'Mantenimiento', equipment: 'Desktop DEL-9012', details: 'Enviado a reparación' },
  { id: 4, date: '2026-01-20 14:20', user: 'Ana Martínez', action: 'Alta', equipment: 'iPad Pro-4456', details: 'Nuevo equipo registrado' },
  { id: 5, date: '2026-01-20 11:00', user: 'Roberto Silva', action: 'Asignación', equipment: 'MacBook-8890', details: 'Asignado a Desarrollo' }
];

export function Dashboard() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-foreground mb-1">Dashboard</h1>
        <p className="text-sm text-muted-foreground">Resumen general del inventario de TI</p>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KPICard 
          title="Total Equipos" 
          value={147}
          icon={Package}
          trend={{ value: '+5 este mes', isPositive: true }}
        />
        <KPICard 
          title="Equipos Disponibles" 
          value={38}
          icon={CheckCircle}
          trend={{ value: '-3 desde ayer', isPositive: false }}
        />
        <KPICard 
          title="Equipos Asignados" 
          value={95}
          icon={UserCircle}
          trend={{ value: '+8 esta semana', isPositive: true }}
        />
        <KPICard 
          title="En Reparación" 
          value={14}
          icon={Wrench}
          trend={{ value: '+2 pendientes', isPositive: false }}
        />
      </div>

      {/* Chart Section */}
      <Card className="p-6">
        <h3 className="text-lg font-semibold mb-4">Equipos por Sucursal</h3>
        <div className="h-80">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={equipmentByBranch}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis 
                dataKey="name" 
                tick={{ fill: '#64748b', fontSize: 12 }}
                axisLine={{ stroke: '#e2e8f0' }}
              />
              <YAxis 
                tick={{ fill: '#64748b', fontSize: 12 }}
                axisLine={{ stroke: '#e2e8f0' }}
              />
              <Tooltip 
                contentStyle={{ 
                  backgroundColor: '#ffffff', 
                  border: '1px solid #e2e8f0',
                  borderRadius: '8px',
                  fontSize: '14px'
                }}
                cursor={{ fill: '#f1f5f9' }}
              />
              <Bar dataKey="equipos" fill="#0056b3" radius={[8, 8, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </Card>

      {/* Recent Movements Table */}
      <Card className="p-6">
        <h3 className="text-lg font-semibold mb-4">Últimos Movimientos (Auditoría)</h3>
        <div className="border rounded-lg">
          <Table>
            <TableHeader>
              <TableRow className="bg-muted/50">
                <TableHead className="font-semibold">Fecha/Hora</TableHead>
                <TableHead className="font-semibold">Usuario</TableHead>
                <TableHead className="font-semibold">Acción</TableHead>
                <TableHead className="font-semibold">Equipo</TableHead>
                <TableHead className="font-semibold">Detalles</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {recentMovements.map((movement) => (
                <TableRow key={movement.id} className="hover:bg-muted/30">
                  <TableCell className="text-sm">{movement.date}</TableCell>
                  <TableCell className="text-sm">{movement.user}</TableCell>
                  <TableCell>
                    <span className={`inline-flex px-2 py-1 rounded-full text-xs font-medium ${
                      movement.action === 'Asignación' ? 'bg-blue-100 text-blue-700' :
                      movement.action === 'Devolución' ? 'bg-green-100 text-green-700' :
                      movement.action === 'Mantenimiento' ? 'bg-yellow-100 text-yellow-700' :
                      'bg-gray-100 text-gray-700'
                    }`}>
                      {movement.action}
                    </span>
                  </TableCell>
                  <TableCell className="text-sm font-medium">{movement.equipment}</TableCell>
                  <TableCell className="text-sm text-muted-foreground">{movement.details}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      </Card>
    </div>
  );
}

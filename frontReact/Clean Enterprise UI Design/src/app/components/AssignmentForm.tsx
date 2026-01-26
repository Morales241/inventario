import { useState } from "react";
import { Card } from "@/app/components/ui/card";
import { Input } from "@/app/components/ui/input";
import { Button } from "@/app/components/ui/button";
import { Label } from "@/app/components/ui/label";
import { Search, UserCircle, Laptop, Check, ArrowRight } from "lucide-react";

const mockEmployees = [
  { id: 1, name: 'Juan Pérez', position: 'Desarrollador Senior', department: 'Tecnología', photo: '' },
  { id: 2, name: 'María García', position: 'Gerente de Marketing', department: 'Marketing', photo: '' },
  { id: 3, name: 'Carlos López', position: 'Analista de Datos', department: 'Analytics', photo: '' },
  { id: 4, name: 'Ana Martínez', position: 'Diseñadora UX', department: 'Diseño', photo: '' },
  { id: 5, name: 'Roberto Silva', position: 'Project Manager', department: 'Proyectos', photo: '' }
];

const mockEquipment = [
  { id: 1, serial: 'LAP-HP-2341', brand: 'HP', model: 'ProBook 450 G8', type: 'Laptop', status: 'Disponible' },
  { id: 2, serial: 'IPH-13-7823', brand: 'Apple', model: 'iPhone 13', type: 'Móvil', status: 'Disponible' },
  { id: 3, serial: 'IPD-PRO-4456', brand: 'Apple', model: 'iPad Pro 11"', type: 'Tablet', status: 'Disponible' },
  { id: 4, serial: 'LAP-LEN-5567', brand: 'Lenovo', model: 'ThinkPad X1', type: 'Laptop', status: 'Disponible' }
];

export function AssignmentForm() {
  const [employeeSearch, setEmployeeSearch] = useState('');
  const [equipmentSearch, setEquipmentSearch] = useState('');
  const [selectedEmployee, setSelectedEmployee] = useState<typeof mockEmployees[0] | null>(null);
  const [selectedEquipment, setSelectedEquipment] = useState<typeof mockEquipment[0] | null>(null);
  const [deliveryDate, setDeliveryDate] = useState('');
  const [returnDate, setReturnDate] = useState('');

  const filteredEmployees = mockEmployees.filter(emp =>
    emp.name.toLowerCase().includes(employeeSearch.toLowerCase()) ||
    emp.position.toLowerCase().includes(employeeSearch.toLowerCase())
  );

  const filteredEquipment = mockEquipment.filter(eq =>
    eq.serial.toLowerCase().includes(equipmentSearch.toLowerCase()) ||
    eq.model.toLowerCase().includes(equipmentSearch.toLowerCase())
  );

  const handleConfirmAssignment = () => {
    if (selectedEmployee && selectedEquipment && deliveryDate) {
      console.log('Assignment confirmed:', {
        employee: selectedEmployee,
        equipment: selectedEquipment,
        deliveryDate,
        returnDate
      });
      // Reset form
      setSelectedEmployee(null);
      setSelectedEquipment(null);
      setDeliveryDate('');
      setReturnDate('');
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-foreground mb-1">Asignación de Equipo</h1>
        <p className="text-sm text-muted-foreground">Asignar equipos a trabajadores</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left Column - Employee Search */}
        <Card className="p-6">
          <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
            <UserCircle className="w-5 h-5 text-primary" />
            Seleccionar Trabajador
          </h3>

          <div className="space-y-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Buscar por nombre o puesto"
                value={employeeSearch}
                onChange={(e) => setEmployeeSearch(e.target.value)}
                className="pl-9"
              />
            </div>

            {selectedEmployee ? (
              <div className="border-2 border-primary rounded-lg p-4 bg-primary/5">
                <div className="flex items-center gap-4">
                  <div className="w-16 h-16 rounded-full bg-primary flex items-center justify-center text-primary-foreground text-xl font-semibold">
                    {selectedEmployee.name.split(' ').map(n => n[0]).join('')}
                  </div>
                  <div className="flex-1">
                    <h4 className="font-semibold text-foreground">{selectedEmployee.name}</h4>
                    <p className="text-sm text-muted-foreground">{selectedEmployee.position}</p>
                    <p className="text-xs text-muted-foreground mt-1">Departamento: {selectedEmployee.department}</p>
                  </div>
                  <Check className="w-6 h-6 text-primary" />
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setSelectedEmployee(null)}
                  className="w-full mt-3"
                >
                  Cambiar Trabajador
                </Button>
              </div>
            ) : (
              <div className="max-h-96 overflow-y-auto space-y-2">
                {filteredEmployees.map((employee) => (
                  <div
                    key={employee.id}
                    onClick={() => setSelectedEmployee(employee)}
                    className="border rounded-lg p-3 hover:bg-muted/50 cursor-pointer transition-colors"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-12 h-12 rounded-full bg-muted flex items-center justify-center text-muted-foreground font-semibold">
                        {employee.name.split(' ').map(n => n[0]).join('')}
                      </div>
                      <div className="flex-1">
                        <h4 className="font-medium text-sm">{employee.name}</h4>
                        <p className="text-xs text-muted-foreground">{employee.position}</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </Card>

        {/* Right Column - Equipment Search */}
        <Card className="p-6">
          <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
            <Laptop className="w-5 h-5 text-primary" />
            Seleccionar Equipo
          </h3>

          <div className="space-y-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Buscar por serie o modelo"
                value={equipmentSearch}
                onChange={(e) => setEquipmentSearch(e.target.value)}
                className="pl-9"
              />
            </div>

            {selectedEquipment ? (
              <div className="border-2 border-primary rounded-lg p-4 bg-primary/5">
                <div className="space-y-2">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <h4 className="font-semibold text-foreground">{selectedEquipment.model}</h4>
                      <p className="text-sm text-muted-foreground">{selectedEquipment.brand}</p>
                      <p className="text-xs font-mono text-muted-foreground mt-1">{selectedEquipment.serial}</p>
                    </div>
                    <Check className="w-6 h-6 text-primary" />
                  </div>
                  <div className="flex items-center gap-2 pt-2">
                    <span className="inline-flex px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-700">
                      {selectedEquipment.type}
                    </span>
                    <span className="inline-flex px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-700">
                      {selectedEquipment.status}
                    </span>
                  </div>
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setSelectedEquipment(null)}
                  className="w-full mt-3"
                >
                  Cambiar Equipo
                </Button>
              </div>
            ) : (
              <div className="max-h-96 overflow-y-auto space-y-2">
                {filteredEquipment.map((equipment) => (
                  <div
                    key={equipment.id}
                    onClick={() => setSelectedEquipment(equipment)}
                    className="border rounded-lg p-3 hover:bg-muted/50 cursor-pointer transition-colors"
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <h4 className="font-medium text-sm">{equipment.model}</h4>
                        <p className="text-xs text-muted-foreground">{equipment.brand}</p>
                        <p className="text-xs font-mono text-muted-foreground mt-1">{equipment.serial}</p>
                      </div>
                      <span className="inline-flex px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-700">
                        {equipment.status}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </Card>
      </div>

      {/* Date Selection and Confirm Button */}
      <Card className="p-6">
        <h3 className="text-lg font-semibold mb-4">Detalles de la Asignación</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
          <div className="space-y-2">
            <Label htmlFor="deliveryDate">Fecha de Entrega *</Label>
            <Input
              id="deliveryDate"
              type="date"
              value={deliveryDate}
              onChange={(e) => setDeliveryDate(e.target.value)}
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="returnDate">Fecha de Devolución (Opcional)</Label>
            <Input
              id="returnDate"
              type="date"
              value={returnDate}
              onChange={(e) => setReturnDate(e.target.value)}
            />
          </div>
        </div>

        {selectedEmployee && selectedEquipment ? (
          <div className="bg-muted/30 rounded-lg p-4 mb-4">
            <h4 className="text-sm font-semibold mb-3">Resumen de Asignación:</h4>
            <div className="flex items-center justify-center gap-4 text-sm">
              <div className="flex items-center gap-2 px-3 py-2 bg-card rounded-lg border">
                <UserCircle className="w-4 h-4 text-primary" />
                <span className="font-medium">{selectedEmployee.name}</span>
              </div>
              <ArrowRight className="w-5 h-5 text-muted-foreground" />
              <div className="flex items-center gap-2 px-3 py-2 bg-card rounded-lg border">
                <Laptop className="w-4 h-4 text-primary" />
                <span className="font-medium">{selectedEquipment.serial}</span>
              </div>
            </div>
          </div>
        ) : null}

        <Button
          onClick={handleConfirmAssignment}
          disabled={!selectedEmployee || !selectedEquipment || !deliveryDate}
          className="w-full bg-primary hover:bg-primary/90 h-12 text-base gap-2"
        >
          <Check className="w-5 h-5" />
          Confirmar Asignación
        </Button>
      </Card>
    </div>
  );
}

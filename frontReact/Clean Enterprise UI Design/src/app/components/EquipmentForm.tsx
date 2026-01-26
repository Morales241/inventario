import { useState } from "react";
import { Card } from "@/app/components/ui/card";
import { Input } from "@/app/components/ui/input";
import { Button } from "@/app/components/ui/button";
import { Label } from "@/app/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/app/components/ui/select";
import { Checkbox } from "@/app/components/ui/checkbox";
import { ArrowLeft, Save } from "lucide-react";

interface EquipmentFormProps {
  onBack: () => void;
}

export function EquipmentForm({ onBack }: EquipmentFormProps) {
  const [equipmentType, setEquipmentType] = useState<'mobile' | 'desktop' | 'other'>('mobile');
  
  // General fields
  const [brand, setBrand] = useState('');
  const [model, setModel] = useState('');
  const [serialNumber, setSerialNumber] = useState('');
  const [status, setStatus] = useState('new');
  const [purchaseDate, setPurchaseDate] = useState('');

  // Mobile specific
  const [imei, setImei] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [hasCase, setHasCase] = useState(false);
  const [hasCharger, setHasCharger] = useState(false);

  // Desktop specific
  const [hostname, setHostname] = useState('');
  const [warrantyEnd, setWarrantyEnd] = useState('');
  const [associatedAccount, setAssociatedAccount] = useState('');

  // Other specific
  const [customField1, setCustomField1] = useState('');
  const [customField2, setCustomField2] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Form submitted');
    onBack();
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="sm" onClick={onBack} className="gap-2">
          <ArrowLeft className="w-4 h-4" />
          Volver
        </Button>
        <div>
          <h1 className="text-2xl font-semibold text-foreground mb-1">Alta de Equipo</h1>
          <p className="text-sm text-muted-foreground">Registrar nuevo equipo en el inventario</p>
        </div>
      </div>

      <form onSubmit={handleSubmit}>
        {/* General Section */}
        <Card className="p-6 mb-6">
          <h3 className="text-lg font-semibold mb-4 pb-2 border-b">Información General</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="brand">Marca</Label>
              <Input
                id="brand"
                value={brand}
                onChange={(e) => setBrand(e.target.value)}
                placeholder="Ej: HP, Dell, Apple"
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="model">Modelo</Label>
              <Input
                id="model"
                value={model}
                onChange={(e) => setModel(e.target.value)}
                placeholder="Ej: ProBook 450 G8"
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="serial">No. de Serie</Label>
              <Input
                id="serial"
                value={serialNumber}
                onChange={(e) => setSerialNumber(e.target.value)}
                placeholder="Ej: LAP-HP-2341"
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="status">Estado</Label>
              <Select value={status} onValueChange={setStatus}>
                <SelectTrigger id="status">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="new">Nuevo</SelectItem>
                  <SelectItem value="used">Usado</SelectItem>
                  <SelectItem value="decommissioned">Baja</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="purchaseDate">Fecha de Compra</Label>
              <Input
                id="purchaseDate"
                type="date"
                value={purchaseDate}
                onChange={(e) => setPurchaseDate(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="equipmentType">Tipo de Equipo</Label>
              <Select 
                value={equipmentType} 
                onValueChange={(value) => setEquipmentType(value as 'mobile' | 'desktop' | 'other')}
              >
                <SelectTrigger id="equipmentType">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="mobile">Móvil</SelectItem>
                  <SelectItem value="desktop">Escritorio</SelectItem>
                  <SelectItem value="other">Otro</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </Card>

        {/* Dynamic Section - Mobile */}
        {equipmentType === 'mobile' && (
          <Card className="p-6 mb-6 border-l-4 border-l-primary">
            <h3 className="text-lg font-semibold mb-4 pb-2 border-b flex items-center gap-2">
              <span className="inline-flex items-center justify-center w-6 h-6 rounded-full bg-primary text-primary-foreground text-xs">
                M
              </span>
              Información Específica - Móvil
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="imei">IMEI</Label>
                <Input
                  id="imei"
                  value={imei}
                  onChange={(e) => setImei(e.target.value)}
                  placeholder="15 dígitos"
                  maxLength={15}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="phoneNumber">Número Celular</Label>
                <Input
                  id="phoneNumber"
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                  placeholder="+52 123 456 7890"
                />
              </div>

              <div className="flex items-center space-x-2 pt-6">
                <Checkbox 
                  id="hasCase" 
                  checked={hasCase}
                  onCheckedChange={(checked) => setHasCase(checked as boolean)}
                />
                <Label htmlFor="hasCase" className="cursor-pointer">
                  Incluye Funda
                </Label>
              </div>

              <div className="flex items-center space-x-2 pt-6">
                <Checkbox 
                  id="hasCharger" 
                  checked={hasCharger}
                  onCheckedChange={(checked) => setHasCharger(checked as boolean)}
                />
                <Label htmlFor="hasCharger" className="cursor-pointer">
                  Incluye Cargador
                </Label>
              </div>
            </div>
          </Card>
        )}

        {/* Dynamic Section - Desktop */}
        {equipmentType === 'desktop' && (
          <Card className="p-6 mb-6 border-l-4 border-l-blue-500">
            <h3 className="text-lg font-semibold mb-4 pb-2 border-b flex items-center gap-2">
              <span className="inline-flex items-center justify-center w-6 h-6 rounded-full bg-blue-500 text-white text-xs">
                D
              </span>
              Información Específica - Escritorio
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="hostname">Nombre de Equipo (Hostname)</Label>
                <Input
                  id="hostname"
                  value={hostname}
                  onChange={(e) => setHostname(e.target.value)}
                  placeholder="Ej: WS-MKT-001"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="warrantyEnd">Fin de Garantía</Label>
                <Input
                  id="warrantyEnd"
                  type="date"
                  value={warrantyEnd}
                  onChange={(e) => setWarrantyEnd(e.target.value)}
                />
              </div>

              <div className="space-y-2 md:col-span-2">
                <Label htmlFor="associatedAccount">Cuenta Asociada</Label>
                <Input
                  id="associatedAccount"
                  value={associatedAccount}
                  onChange={(e) => setAssociatedAccount(e.target.value)}
                  placeholder="Ej: admin@empresa.com o dominio\\usuario"
                />
              </div>
            </div>
          </Card>
        )}

        {/* Dynamic Section - Other */}
        {equipmentType === 'other' && (
          <Card className="p-6 mb-6 border-l-4 border-l-amber-500">
            <h3 className="text-lg font-semibold mb-4 pb-2 border-b flex items-center gap-2">
              <span className="inline-flex items-center justify-center w-6 h-6 rounded-full bg-amber-500 text-white text-xs">
                O
              </span>
              Información Específica - Otro Equipo
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="customField1">Campo Personalizado 1</Label>
                <Input
                  id="customField1"
                  value={customField1}
                  onChange={(e) => setCustomField1(e.target.value)}
                  placeholder="Información adicional"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="customField2">Campo Personalizado 2</Label>
                <Input
                  id="customField2"
                  value={customField2}
                  onChange={(e) => setCustomField2(e.target.value)}
                  placeholder="Información adicional"
                />
              </div>
            </div>
          </Card>
        )}

        {/* Action Buttons */}
        <div className="flex items-center gap-3 justify-end">
          <Button type="button" variant="outline" onClick={onBack}>
            Cancelar
          </Button>
          <Button type="submit" className="bg-primary hover:bg-primary/90 gap-2">
            <Save className="w-4 h-4" />
            Guardar Equipo
          </Button>
        </div>
      </form>
    </div>
  );
}

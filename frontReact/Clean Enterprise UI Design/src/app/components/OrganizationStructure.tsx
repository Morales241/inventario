import { useState } from "react";
import { Card } from "@/app/components/ui/card";
import { Input } from "@/app/components/ui/input";
import { Button } from "@/app/components/ui/button";
import { Label } from "@/app/components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/app/components/ui/tabs";
import { 
  Building2, 
  ChevronRight, 
  ChevronDown, 
  MapPin, 
  Briefcase, 
  Users2,
  Save,
  Trash2,
  Plus
} from "lucide-react";
import { cn } from "@/app/components/ui/utils";

interface TreeNode {
  id: string;
  name: string;
  type: 'company' | 'branch' | 'department' | 'position';
  location?: string;
  children?: TreeNode[];
  employeeCount?: number;
}

const mockOrgData: TreeNode[] = [
  {
    id: 'comp-1',
    name: 'Grupo Empresarial TechCorp',
    type: 'company',
    location: 'Ciudad de México',
    children: [
      {
        id: 'branch-1',
        name: 'Matriz - CDMX',
        type: 'branch',
        location: 'Av. Reforma 123, CDMX',
        employeeCount: 45,
        children: [
          {
            id: 'dept-1',
            name: 'Tecnología',
            type: 'department',
            employeeCount: 12,
            children: [
              { id: 'pos-1', name: 'Desarrollador Senior', type: 'position', employeeCount: 4 },
              { id: 'pos-2', name: 'Desarrollador Junior', type: 'position', employeeCount: 6 },
              { id: 'pos-3', name: 'DevOps Engineer', type: 'position', employeeCount: 2 }
            ]
          },
          {
            id: 'dept-2',
            name: 'Marketing',
            type: 'department',
            employeeCount: 8,
            children: [
              { id: 'pos-4', name: 'Gerente de Marketing', type: 'position', employeeCount: 1 },
              { id: 'pos-5', name: 'Especialista Digital', type: 'position', employeeCount: 5 },
              { id: 'pos-6', name: 'Community Manager', type: 'position', employeeCount: 2 }
            ]
          },
          {
            id: 'dept-3',
            name: 'Recursos Humanos',
            type: 'department',
            employeeCount: 5,
            children: [
              { id: 'pos-7', name: 'Director de RRHH', type: 'position', employeeCount: 1 },
              { id: 'pos-8', name: 'Reclutador', type: 'position', employeeCount: 3 }
            ]
          }
        ]
      },
      {
        id: 'branch-2',
        name: 'Sucursal Norte',
        type: 'branch',
        location: 'Monterrey, N.L.',
        employeeCount: 28,
        children: [
          {
            id: 'dept-4',
            name: 'Ventas',
            type: 'department',
            employeeCount: 15,
            children: [
              { id: 'pos-9', name: 'Ejecutivo de Ventas', type: 'position', employeeCount: 12 },
              { id: 'pos-10', name: 'Gerente de Ventas', type: 'position', employeeCount: 3 }
            ]
          },
          {
            id: 'dept-5',
            name: 'Soporte Técnico',
            type: 'department',
            employeeCount: 8,
            children: [
              { id: 'pos-11', name: 'Técnico de Soporte', type: 'position', employeeCount: 6 },
              { id: 'pos-12', name: 'Supervisor de Soporte', type: 'position', employeeCount: 2 }
            ]
          }
        ]
      },
      {
        id: 'branch-3',
        name: 'Sucursal Occidente',
        type: 'branch',
        location: 'Guadalajara, Jal.',
        employeeCount: 19,
        children: [
          {
            id: 'dept-6',
            name: 'Operaciones',
            type: 'department',
            employeeCount: 12,
            children: [
              { id: 'pos-13', name: 'Coordinador de Operaciones', type: 'position', employeeCount: 2 },
              { id: 'pos-14', name: 'Analista de Procesos', type: 'position', employeeCount: 5 }
            ]
          }
        ]
      }
    ]
  }
];

export function OrganizationStructure() {
  const [expandedNodes, setExpandedNodes] = useState<Set<string>>(new Set(['comp-1', 'branch-1']));
  const [selectedNode, setSelectedNode] = useState<TreeNode | null>(mockOrgData[0].children?.[0] || null);
  const [editedName, setEditedName] = useState(selectedNode?.name || '');
  const [editedLocation, setEditedLocation] = useState(selectedNode?.location || '');

  const toggleNode = (nodeId: string) => {
    const newExpanded = new Set(expandedNodes);
    if (newExpanded.has(nodeId)) {
      newExpanded.delete(nodeId);
    } else {
      newExpanded.add(nodeId);
    }
    setExpandedNodes(newExpanded);
  };

  const handleNodeSelect = (node: TreeNode) => {
    setSelectedNode(node);
    setEditedName(node.name);
    setEditedLocation(node.location || '');
  };

  const getNodeIcon = (type: string) => {
    switch (type) {
      case 'company': return Building2;
      case 'branch': return MapPin;
      case 'department': return Users2;
      case 'position': return Briefcase;
      default: return Building2;
    }
  };

  const getNodeColor = (type: string) => {
    switch (type) {
      case 'company': return 'text-primary';
      case 'branch': return 'text-blue-600';
      case 'department': return 'text-purple-600';
      case 'position': return 'text-amber-600';
      default: return 'text-gray-600';
    }
  };

  const renderTreeNode = (node: TreeNode, level: number = 0) => {
    const isExpanded = expandedNodes.has(node.id);
    const hasChildren = node.children && node.children.length > 0;
    const isSelected = selectedNode?.id === node.id;
    const Icon = getNodeIcon(node.type);

    return (
      <div key={node.id}>
        <div
          onClick={() => handleNodeSelect(node)}
          className={cn(
            "flex items-center gap-2 py-2 px-3 rounded-lg cursor-pointer transition-colors group",
            isSelected ? "bg-primary/10 text-primary" : "hover:bg-muted/50"
          )}
          style={{ paddingLeft: `${level * 20 + 12}px` }}
        >
          {hasChildren ? (
            <button
              onClick={(e) => {
                e.stopPropagation();
                toggleNode(node.id);
              }}
              className="p-0.5 hover:bg-muted rounded"
            >
              {isExpanded ? (
                <ChevronDown className="w-4 h-4 text-muted-foreground" />
              ) : (
                <ChevronRight className="w-4 h-4 text-muted-foreground" />
              )}
            </button>
          ) : (
            <div className="w-5" />
          )}
          
          <Icon className={cn("w-4 h-4", isSelected ? "text-primary" : getNodeColor(node.type))} />
          
          <div className="flex-1 min-w-0">
            <p className={cn(
              "text-sm truncate",
              isSelected ? "font-semibold" : "font-medium"
            )}>
              {node.name}
            </p>
            {node.employeeCount !== undefined && (
              <p className="text-xs text-muted-foreground">
                {node.employeeCount} {node.type === 'position' ? 'empleados' : 'miembros'}
              </p>
            )}
          </div>
        </div>
        
        {hasChildren && isExpanded && (
          <div>
            {node.children!.map(child => renderTreeNode(child, level + 1))}
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-foreground mb-1">Estructura Organizacional</h1>
        <p className="text-sm text-muted-foreground">Gestión jerárquica de empresas, sucursales, departamentos y puestos</p>
      </div>

      <div className="grid grid-cols-12 gap-6">
        {/* Left Panel - Tree Navigation */}
        <div className="col-span-4">
          <Card className="h-[calc(100vh-200px)] flex flex-col">
            <div className="p-4 border-b flex items-center justify-between">
              <h3 className="font-semibold">Árbol de Organización</h3>
              <Button size="sm" variant="ghost" className="h-8 w-8 p-0">
                <Plus className="w-4 h-4" />
              </Button>
            </div>
            <div className="flex-1 overflow-y-auto p-2">
              {mockOrgData.map(node => renderTreeNode(node))}
            </div>
          </Card>
        </div>

        {/* Right Panel - Detail and Edit */}
        <div className="col-span-8">
          {selectedNode ? (
            <Card className="h-[calc(100vh-200px)] flex flex-col">
              <div className="p-6 border-b">
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-3">
                    {(() => {
                      const Icon = getNodeIcon(selectedNode.type);
                      return <Icon className={cn("w-6 h-6", getNodeColor(selectedNode.type))} />;
                    })()}
                    <div>
                      <h2 className="text-xl font-semibold">{selectedNode.name}</h2>
                      <p className="text-sm text-muted-foreground capitalize">
                        {selectedNode.type === 'company' ? 'Empresa' :
                         selectedNode.type === 'branch' ? 'Sucursal' :
                         selectedNode.type === 'department' ? 'Departamento' : 'Puesto'}
                      </p>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <Button variant="outline" size="sm" className="gap-2">
                      <Trash2 className="w-4 h-4" />
                      Eliminar
                    </Button>
                    <Button size="sm" className="bg-primary hover:bg-primary/90 gap-2">
                      <Save className="w-4 h-4" />
                      Guardar Cambios
                    </Button>
                  </div>
                </div>
              </div>

              <Tabs defaultValue="general" className="flex-1 flex flex-col">
                <TabsList className="mx-6 mt-4 w-fit">
                  <TabsTrigger value="general">Información General</TabsTrigger>
                  <TabsTrigger value="children">Elementos Hijos</TabsTrigger>
                </TabsList>

                <TabsContent value="general" className="flex-1 p-6 space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="name">Nombre</Label>
                    <Input
                      id="name"
                      value={editedName}
                      onChange={(e) => setEditedName(e.target.value)}
                      placeholder="Nombre del elemento"
                    />
                  </div>

                  {(selectedNode.type === 'company' || selectedNode.type === 'branch') && (
                    <div className="space-y-2">
                      <Label htmlFor="location">Ubicación</Label>
                      <Input
                        id="location"
                        value={editedLocation}
                        onChange={(e) => setEditedLocation(e.target.value)}
                        placeholder="Dirección o ubicación"
                      />
                    </div>
                  )}

                  <div className="grid grid-cols-2 gap-4 pt-4">
                    <Card className="p-4 bg-muted/30">
                      <p className="text-sm text-muted-foreground mb-1">Total de Empleados</p>
                      <p className="text-2xl font-semibold">{selectedNode.employeeCount || 0}</p>
                    </Card>
                    <Card className="p-4 bg-muted/30">
                      <p className="text-sm text-muted-foreground mb-1">Elementos Hijos</p>
                      <p className="text-2xl font-semibold">{selectedNode.children?.length || 0}</p>
                    </Card>
                  </div>

                  {selectedNode.location && (
                    <div className="pt-4">
                      <Label>Dirección Completa</Label>
                      <div className="flex items-start gap-2 mt-2 p-3 bg-muted/30 rounded-lg">
                        <MapPin className="w-4 h-4 text-primary mt-0.5" />
                        <p className="text-sm">{selectedNode.location}</p>
                      </div>
                    </div>
                  )}
                </TabsContent>

                <TabsContent value="children" className="flex-1 p-6">
                  <div className="space-y-2">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="font-semibold">
                        {selectedNode.type === 'company' ? 'Sucursales' :
                         selectedNode.type === 'branch' ? 'Departamentos' :
                         selectedNode.type === 'department' ? 'Puestos' : 'Elementos'}
                      </h3>
                      <Button size="sm" className="gap-2">
                        <Plus className="w-4 h-4" />
                        Agregar
                      </Button>
                    </div>

                    {selectedNode.children && selectedNode.children.length > 0 ? (
                      <div className="space-y-2">
                        {selectedNode.children.map((child) => {
                          const ChildIcon = getNodeIcon(child.type);
                          return (
                            <Card 
                              key={child.id} 
                              className="p-4 hover:bg-muted/30 cursor-pointer transition-colors"
                              onClick={() => handleNodeSelect(child)}
                            >
                              <div className="flex items-center justify-between">
                                <div className="flex items-center gap-3">
                                  <ChildIcon className={cn("w-5 h-5", getNodeColor(child.type))} />
                                  <div>
                                    <p className="font-medium">{child.name}</p>
                                    {child.employeeCount !== undefined && (
                                      <p className="text-xs text-muted-foreground">
                                        {child.employeeCount} empleados
                                      </p>
                                    )}
                                  </div>
                                </div>
                                <ChevronRight className="w-5 h-5 text-muted-foreground" />
                              </div>
                            </Card>
                          );
                        })}
                      </div>
                    ) : (
                      <div className="text-center py-12">
                        <p className="text-sm text-muted-foreground">
                          No hay elementos hijos
                        </p>
                      </div>
                    )}
                  </div>
                </TabsContent>
              </Tabs>
            </Card>
          ) : (
            <Card className="h-[calc(100vh-200px)] flex items-center justify-center">
              <div className="text-center">
                <Building2 className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                <p className="text-muted-foreground">
                  Selecciona un elemento del árbol para ver sus detalles
                </p>
              </div>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}

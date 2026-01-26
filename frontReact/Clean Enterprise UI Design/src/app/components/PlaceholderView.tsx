import { Card } from "@/app/components/ui/card";
import { Construction } from "lucide-react";

interface PlaceholderViewProps {
  title: string;
  description: string;
}

export function PlaceholderView({ title, description }: PlaceholderViewProps) {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-foreground mb-1">{title}</h1>
        <p className="text-sm text-muted-foreground">{description}</p>
      </div>

      <Card className="p-12">
        <div className="flex flex-col items-center justify-center text-center">
          <div className="w-20 h-20 rounded-full bg-muted flex items-center justify-center mb-4">
            <Construction className="w-10 h-10 text-muted-foreground" />
          </div>
          <h3 className="text-lg font-semibold mb-2">En Desarrollo</h3>
          <p className="text-sm text-muted-foreground max-w-md">
            Esta sección está en desarrollo. Pronto estará disponible con todas sus funcionalidades.
          </p>
        </div>
      </Card>
    </div>
  );
}

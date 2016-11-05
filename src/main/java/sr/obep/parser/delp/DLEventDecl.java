package sr.obep.parser.delp;

import com.espertech.esper.client.soda.CreateSchemaClause;
import com.espertech.esper.client.soda.SchemaColumnDesc;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Var;

import java.io.StringWriter;
import java.util.*;

/**
 * Created by Riccardo on 16/08/16. This class represents the event declaration
 * using DL manchester syntax. - The head consists of the left part of the DL
 * rule. - The body, //TODO parse it, is the right part of the DL rule. - filter_events
 * is a SPARQL-Like constraint for the event instances.
 */
@Data
@EqualsAndHashCode
@ToString
public class DLEventDecl extends EventDecl {

    private String body;
    private int occurrence = -1;

    public DLEventDecl(Node pop) {
        setHead(pop);
    }

    public String toEPLSchema(Set<Var> vars) {
        CreateSchemaClause schema = new CreateSchemaClause();
        schema.setSchemaName(getHead().getURI().replace(getHead().getNameSpace(), "")); //TODO
        schema.setInherits(new HashSet<String>(Arrays.asList(new String[]{"TEvent"})));
        List<SchemaColumnDesc> columns = new ArrayList<SchemaColumnDesc>();
        for (Var var : vars) {
            SchemaColumnDesc scd = new SchemaColumnDesc();
            scd.setArray(false);
            scd.setType("String");
            scd.setName(var.getName());
            columns.add(scd);
        }

        schema.setColumns(columns);
        StringWriter writer = new StringWriter();
        schema.toEPL(writer);

        return writer.toString();
    }

    public String toEPLSchema() {
        return toEPLSchema(new HashSet<Var>());
    }

}

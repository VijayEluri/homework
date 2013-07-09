package fatworm.expr;

import fatworm.db.Schema;
import fatworm.db.Transaction;
import fatworm.scan.Scan;

public class Negate extends Predicate {
	Predicate arg;
	
	public Negate(Predicate arg) {
		this.arg = arg;
		this.type = java.sql.Types.BOOLEAN;
	}
	
	@Override
	public void consumeFields(Schema schema) {
		arg.consumeFields(schema);
	}

	@Override
	public void scan(Transaction tx) {
		arg.scan(tx);
	}

	@Override
	public void consume(Scan scan) {
		arg.consume(scan);
	}

}

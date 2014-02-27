public class CatalogMember {
	/*
	 * Atributo para el Nombre del Atributo.
	 */
	String atriName;

	public String getAtriName() {
		return atriName;
	}

	public void setAtriName(String atriName) {
		this.atriName = atriName;
	}	

	/*
	 * Atributo para el Tipo del Atributo.
	 */
	String atriType;
	
	public String getAtriType() {
		return atriType;
	}

	public void setAtriType(String atriType) {
		this.atriType = atriType;
	}
	
	/*
	 * Atributo para el Nombre de la Tabla
	 */
	String atriRelation;
	
	public String getAtriRelation() {
		return atriRelation;
	}

	public void setAtriRelation(String atriRelation) {
		this.atriRelation = atriRelation;
	}
	
	/*
	 * Atributo para la Longitud del Atributo
	 */
	int atriSize = 1;
	
	public int getAtriSize() {
		return atriSize;
	}

	public void setAtriSize(int atriSize) {
		this.atriSize = atriSize;
	}
	
	/*
	 * Atributo para la Posicion del Atributo
	 */
	int atriPosition;
	
	public int getAtriPosition() {
		return atriPosition;
	}

	public void setAtriPosition(int atriPosition) {
		this.atriPosition = atriPosition;
	}
	
	/*
	 * Atributo para la Restriccion del Atributo
	 */
	String constraint = "ALLOW NULL";
	
	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}
	
	/*
	 * Constructor por defecto del Miembro 
	 * del Catalogo de la Base de Datos.
	 */
	public CatalogMember () {
		
	}
	
	/*
	 * Constructor para el Atributo que sera
	 * agregado al Catalogo de la Base de Datos.
	 */
	public CatalogMember (String _name, String _type, String _relation) {
		atriName = _name;
		atriType = _type;
		atriRelation = _relation;
	}
	
	/*
	 * Constructor para el Atributo que sera
	 * agregado al Catalogo de la Base de Datos.
	 */
	public CatalogMember (String _name, String _type, int _length) 
	{
		atriName = _name;
		atriType = _type;
		atriSize = _length;
	}
}
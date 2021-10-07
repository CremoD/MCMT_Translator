# Bachelor Thesis Project
## An SMT-based formalization of data-aware BPMN

BPM (Business Process Management) is the science of analyzing and optimizing business processes, with the aim of improving the efficiency and competitiveness of the organization. One of the most used techniques in this field is the graphical representation of business pro- cesses, using as standard language BPMN (Business Process Modeling Notation). However, BPMN has a considerable limitation: it is unsuitable to capture the multiple perspective typ- ical of business processes, and in particular the correlation between process and data. As a result, in recent years, many research projects have focused on formalizing models that integrate these two dimensions.
The thesis originates from a research project in this field, which proposed a BPMN ex- tension enriched with data, called DAB (data-aware BPMN). The DAB model represents a compromise between expressiveness and the possibility to perform safety verification tasks. For this latter purpose, the model needs to be translated into a formalism called "array-based system", which in our case is based on the MCMT model checker.
In order to reach the objective, the thesis proposes an object-based model that represents DABs and at the same time automates the translation process into the MCMT formalism. The result is the development of a Java program, which allows the user to model and create a DAB process in an intuitive way but at the same time consistent and flexible. In addition, the program combines a number of methods and algorithms responsible for translating the DAB process into an MCMT specification file, automatically and completely independently from the user. As a result, by just creating an object oriented DAB in Java, a user has the ability to create complex MCMT specification files, ready to be analyzed by the model checker and verify the security of the system with respect to an undesired property.

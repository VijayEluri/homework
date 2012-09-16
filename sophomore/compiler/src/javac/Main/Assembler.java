package javac.Main;

import javac.absyn.ParameterDecl;
import javac.atom.*;

import java.util.*;
import java.util.Map.Entry;

import javac.quad.*;

public class Assembler {
	private int saved_reg_count = 1;
	private int frame_size;
	private int local_pos;
	private int reg_pos;
	private Map<Temp, Integer> position;
	private Set<Temp> params;
	private List<String> code;
	
	private int used_reg_count = 26;
	private HashSet<Temp>[] reg_content;
	private String[] reg_name;
	private Map<Temp, Integer> tmp_content;
	private List<Atom> current_basicblock;
	private Set<Temp> alive, nextUse;
	
	public Assembler(Translator func) {
		//System.out.println(func.function_label);
		
		position = new HashMap<Temp, Integer>();
		nextUse = new HashSet<Temp>();
		alive = new HashSet<Temp>();
		code = new ArrayList<String>();
		params = new HashSet<Temp>();
		reg_name = new String[used_reg_count];
		tmp_content = new HashMap<Temp, Integer>();
		reg_content = new HashSet[used_reg_count];
		current_basicblock = new ArrayList<Atom>();
		for (int i = 0; i < used_reg_count; ++ i)
			reg_content[i] = new HashSet<Temp>();
		reg_name[0] = "$t0"; reg_name[1] = "$t1";
		reg_name[2] = "$t2"; reg_name[3] = "$t3";
		reg_name[4] = "$t4"; reg_name[5] = "$t5";
		reg_name[6] = "$t6"; reg_name[7] = "$t7";
		reg_name[8] = "$t8"; reg_name[9] = "$t9";
		reg_name[10] = "$v1"; reg_name[11] = "$a1";
		reg_name[12] = "$a2"; reg_name[13] = "$a3";
		reg_name[14] = "$s0"; reg_name[15] = "$s1";
		reg_name[16] = "$s2"; reg_name[17] = "$s3";
		reg_name[18] = "$s4"; reg_name[19] = "$s5";
		reg_name[20] = "$s6"; reg_name[21] = "$s7"; reg_name[22] = "$gp";
		reg_name[23] = "$k0"; reg_name[24] = "$k1"; reg_name[25] = "$s8";
		
		reg_pos = func.arg_size * 4;
		local_pos = reg_pos + saved_reg_count * 4;
		allocate_temporary(func);
		frame_size = local_pos + position.size() * 4;
		
		code.add(func.getName() + ":");
		code.add("addi $sp, $sp," + (-frame_size));
		int index = 0;
		for (ParameterDecl param : func.def.head.parameterList.parameterDeclarations) {
			Temp t = func.env.get(param.name);
			position.put(t, frame_size + index * 4);
			index ++;
		}
		
		int value = 0;
		for (Atom atom : func.atoms)
			if (atom instanceof CALL) value += position.size() * 2 + 1;
		
		/*System.out.println(position.size());
		System.out.println(func.atoms.size() + " " + value);
		for (Atom atom : func.atoms)
			System.out.println(atom.toString());*/
			
		if (position.size() <= used_reg_count && func.atoms.size() > value || func.function_label.toString().startsWith("print")) {
			full_register_allocation(func);
		} else {
			for (Atom atom : func.atoms) {
				//System.out.println(atom.toString());
				if (atom instanceof BRANCH) ((BRANCH)atom).label.avaiable = true;
				else if (atom instanceof COMP) ((COMP)atom).label.avaiable = true;
				else if (atom instanceof JMP) ((JMP)atom).label.avaiable = true;
			}
			current_basicblock.clear();
			for (int i = 0; i < used_reg_count; ++ i)
				reg_content[i].clear();
			tmp_content.clear();
			for (Atom atom : func.atoms) {
				if (atom instanceof ARI) current_basicblock.add(atom);
				else if (atom instanceof IARI) current_basicblock.add(atom);
				else if (atom instanceof LA) current_basicblock.add(atom);
				else if (atom instanceof LI) current_basicblock.add(atom);
				else if (atom instanceof MEM) current_basicblock.add(atom);
				else if (atom instanceof SARI) current_basicblock.add(atom);
				else if (atom instanceof RETURN) current_basicblock.add(atom);
				else if (atom instanceof CALL) current_basicblock.add(atom);
				else if (atom instanceof BRANCH) {
					current_basicblock.add(atom);
					translate();
				}
				else if (atom instanceof JMP) {
					current_basicblock.add(atom);
					translate();
				}
				else if (atom instanceof COMP) {
					current_basicblock.add(atom);
					translate();
				}
				else if (atom instanceof LabelAtom) {
					if (((LabelAtom)atom).label.avaiable) {						
						translate();
						//System.out.println(atom.toString());
						code.add(atom.toString());
					} else current_basicblock.add(atom);
				}
			}
			//store_temporary();
			translate();
		}
		
		code.add("addi $sp, $sp," + frame_size);
		code.add("jr $ra");
	}
	
	private void translate() {
		//System.out.println("--------------------");
		
		nextUse.clear();
		alive.clear();
		for (Atom atom : current_basicblock)
			atom.touch(alive);
		
		//System.out.println(alive.toString());
		
		for (int i = 0; i < used_reg_count; ++ i)
			reg_content[i].clear();
		tmp_content.clear();
		
		for (int i = current_basicblock.size() - 1; i >= 0; -- i) {
			Atom atom = current_basicblock.get(i);
			atom.handle_liveness(alive);
		}
		for (Temp t : alive) nextUse.add(t);
		
		boolean stored = false;
		for (Atom atom : current_basicblock) {
			//System.out.println(atom.toString());
			
			stored = false;
			atom.handle_nextUse(nextUse);
			if (atom instanceof ARI) asm((ARI)atom);
			else if (atom instanceof CALL) asm((CALL)atom);
			else if (atom instanceof IARI) asm((IARI)atom);
			else if (atom instanceof LA) asm((LA)atom);
			else if (atom instanceof LabelAtom) asm((LabelAtom)atom);
			else if (atom instanceof LI) asm((LI)atom);
			else if (atom instanceof MEM) asm((MEM)atom);
			else if (atom instanceof RETURN) asm((RETURN)atom);
			else if (atom instanceof SARI) asm((SARI)atom);
			else if (atom instanceof JMP) {
				store_temporary(); stored = true;
				asm((JMP)atom);
			} else if (atom instanceof BRANCH) {
				store_temporary(); stored = true;
				asm((BRANCH)atom);
			} else if (atom instanceof COMP) {
				store_temporary(); stored = true;
				asm((COMP)atom);
			}
			//System.out.println(tmp_content.toString());
			//System.out.println(nextUse.toString());
		}
		if (!stored) store_temporary();
		
		current_basicblock.clear();
	}
	
	private void store_temporary() {
		for (Map.Entry<Temp, Integer> ent : tmp_content.entrySet())
			code.add("sw " + reg_name[ent.getValue()] + ", " + position.get(ent.getKey()) + "($sp)");
		/*tmp_content.clear();
		for (int i = 0; i < used_reg_count; ++ i)
			reg_content[i].clear();*/
	}
	
	private void allocate(Temp t) {
		if (!position.containsKey(t) && !params.contains(t))
			position.put(t, Integer.valueOf(local_pos + position.size() * 4));
	}
	
	private void allocate_temporary(Translator func) {
		for (ParameterDecl param : func.def.head.parameterList.parameterDeclarations)
			params.add(func.env.get(param.name));
		for (Atom atom : func.atoms) {
			if (atom instanceof LI) {
				allocate(((LI)atom).dst);
			} else if (atom instanceof LA) {
				allocate(((LA)atom).dst);
			} else if (atom instanceof BRANCH) {
				allocate(((BRANCH)atom).src);
			} else if (atom instanceof CALL) {
				allocate(((CALL)atom).result);
				for (Temp t : ((CALL)atom).params)
					allocate(t);
			} else if (atom instanceof COMP) {
				allocate(((COMP)atom).src1);
				allocate(((COMP)atom).src2);
			} else if (atom instanceof IARI) {
				allocate(((IARI)atom).src1);
				allocate(((IARI)atom).dst);
			} else if (atom instanceof MEM) {
				allocate(((MEM)atom).src);
				allocate(((MEM)atom).dst);
			} else if (atom instanceof SARI) {
				allocate(((SARI)atom).src);
				allocate(((SARI)atom).dst);
			} else if (atom instanceof RETURN && ((RETURN)atom).value instanceof TempOprand) {
				allocate(((TempOprand)((RETURN)atom).value).temp);
			} else if (atom instanceof ARI) {
				allocate(((ARI)atom).src1);
				allocate(((ARI)atom).src2);
				allocate(((ARI)atom).dst);
			}
		}
	}
	
	private void asm(SARI atom) {
		if (!nextUse.contains(atom.dst)) return;
		
		int src;
		if (!tmp_content.containsKey(atom.dst)) src = pickReg(atom.src);
		else src = pickReg(atom.src, tmp_content.get(atom.dst));
		if (atom.type == "move") {
			if (tmp_content.containsKey(atom.dst)) {
				int dst = tmp_content.get(atom.dst);
				if (dst == src) return;
				reg_content[dst].remove(atom.dst);
				tmp_content.remove(atom.dst);
			}
			tmp_content.put(atom.dst, src);
			reg_content[src].add(atom.dst);
		} else {
			if (!nextUse.contains(atom.src)) {
				int dst = pickReg2(atom.dst);
				code.add(atom.type + " " + reg_name[dst] + ", " + reg_name[src]);
			} else {
				int dst;
				if (!atom.dst.equal(atom.src) && tmp_content.containsKey(atom.dst) && tmp_content.get(atom.dst) == src) {
					reg_content[tmp_content.get(atom.dst)].remove(atom.dst);
					tmp_content.remove(atom.dst);
				}
				dst = pickReg2(atom.dst, src);
				code.add(atom.type + " " + reg_name[dst] + ", " + reg_name[src]);
			}
		}
	}

	private void asm(RETURN atom) {
		if (atom.value instanceof Const) {
			code.add("li $v0, " + ((Const)atom.value).value);
		} else {
			Temp t = ((TempOprand)atom.value).temp;
			if (tmp_content.containsKey(t)) {
				String reg = reg_name[tmp_content.get(t)];
				if (reg != "$v0")
					code.add("move $v0, " + reg);
			} else
				code.add("lw $v0, " + position.get(t) + "($sp)");
		}
		
		code.add("addi $sp, $sp," + frame_size);
		code.add("jr $ra");
	}

	private void asm(LI atom) {
		if (!nextUse.contains(atom.dst)) return;
		code.add("li " + reg_name[pickReg2(atom.dst)] + ", " + atom.imm);
	}

	private void asm(MEM atom) {
		if (atom.type.charAt(0) == 'l' && !nextUse.contains(atom.dst)) return;
		
		int src, dst;
		if (tmp_content.containsKey(atom.dst))
			src = pickReg(atom.src, tmp_content.get(atom.dst));
		else src = pickReg(atom.src);
		
		if (atom.type.startsWith("s")) {
			/*if (nextUse.contains(atom.src)) */dst = pickReg(atom.dst, src);
			//else dst = pickReg(atom.dst);
		} else {
			if (nextUse.contains(atom.src)) dst = pickReg2(atom.dst, src);
			else dst = pickReg2(atom.dst);
		}

		code.add(atom.type + " " + reg_name[dst] + ", " + atom.offset + "(" + reg_name[src] + ")");

	}

	private void asm(LabelAtom atom) {
		code.add(atom.toString());
	}

	private void asm(LA atom) {
		if (!nextUse.contains(atom.dst)) return;
		
		code.add("la " + reg_name[pickReg2(atom.dst)] + ", " + atom.src.toString());
	}

	private void asm(JMP atom) {
		code.add(atom.toString());
	}

	private void asm(IARI atom) {
		if (!nextUse.contains(atom.dst)) return;
		
		int src;
		if (!tmp_content.containsKey(atom.dst)) src = pickReg(atom.src1);
		else src = pickReg(atom.src1, tmp_content.get(atom.dst));
		if (!nextUse.contains(atom.src1)) {
			int dst = pickReg2(atom.dst);
			code.add(atom.type + " " + reg_name[dst] + ", " + reg_name[src] + ", " + atom.src2);
		} else {
			int dst;
			if (!atom.dst.equal(atom.src1) && tmp_content.containsKey(atom.dst) && tmp_content.get(atom.dst) == src) {
				reg_content[tmp_content.get(atom.dst)].remove(atom.dst);
				tmp_content.remove(atom.dst);
			}
			dst = pickReg2(atom.dst, src);
			code.add(atom.type + " " + reg_name[dst] + ", " + reg_name[src] + ", " + atom.src2);
		}
	}

	private void asm(COMP atom) {
		int src1, src2;
		if (tmp_content.containsKey(atom.src2)) src1 = pickReg(atom.src1, tmp_content.get(atom.src2));
		else src1 = pickReg(atom.src1);
		src2 = pickReg(atom.src2, src1);
		code.add(atom.type + " " + reg_name[src1] + ", " + reg_name[src2] + ", " + atom.label);
	}

	private void asm(CALL atom) {
		// TODO Auto-generated method stub
		//if (!nextUse.contains(atom.result)) return;
		
		//String dst = reg_name[pickReg2(atom.result)];
		
		if (atom.function.toString() == "_malloc") {
			Temp t = atom.params[0];
			if (tmp_content.containsKey(t)) code.add("move $a0, " + reg_name[tmp_content.get(t)]);
			else code.add("lw $a0, " + position.get(atom.params[0]) + "($sp)");
			code.add("li $v0, 9");
			code.add("syscall");
			//code.add("sw $v0, " + dst_pos + "($sp)");
			code.add("move " + reg_name[pickReg2(atom.result)] + ", $v0");
		} else if (atom.function.toString() == "printInt"){
			Temp t = atom.params[0];
			if (tmp_content.containsKey(t)) code.add("move $a0, " + reg_name[tmp_content.get(t)]);
			else code.add("lw $a0, " + position.get(atom.params[0]) + "($sp)");
			code.add("li $v0, 1");
			code.add("syscall");
		} else if (atom.function.toString() == "printChar"){
			//code.add("lb $a0, " + position.get(atom.params[0]) + "($sp)");
			Temp t = atom.params[0];
			if (tmp_content.containsKey(t)) code.add("move $a0, " + reg_name[tmp_content.get(t)]);
			else code.add("lb $a0, " + position.get(atom.params[0]) + "($sp)");
			code.add("sb $a0, 0($sp)");
			code.add("sb $zero, 1($sp)");
			code.add("move $a0, $sp");
			code.add("li $v0, 4");
			code.add("syscall");
			
		} else if (atom.function.toString() == "printString"){
			Temp t = atom.params[0];
			if (tmp_content.containsKey(t)) code.add("move $a0, " + reg_name[tmp_content.get(t)]);
			else code.add("lw $a0, " + position.get(atom.params[0]) + "($sp)");
			code.add("li $v0, 4");
			code.add("syscall");
			
		} else if (atom.function.toString() == "readInt"){
			code.add("li $v0, 5");
			code.add("syscall");
			//code.add("sw $a0, " + position.get(atom.params[0]) + "($sp)");
			code.add("move " + reg_name[pickReg2(atom.result)] + ", $v0");
		} else {
			//Pre-do
			
			code.add("sw $ra, " + reg_pos + "($sp)");
			int index = 0;
			for (Temp t : atom.params) {
				if (tmp_content.containsKey(t)) {
					String reg = reg_name[tmp_content.get(t)];
					code.add("sw " + reg + ", " + index * 4 + "($sp)");
				} else {
					int src_pos = position.get(t);
					code.add("lw $v0, " + src_pos + "($sp)");
					code.add("sw $v0, " + index * 4 + "($sp)");
				}
				
				index ++;
			}

			Set<Entry<Temp, Integer>> tmp_set = new HashSet<Entry<Temp, Integer>>(tmp_content.entrySet());
			for (Map.Entry<Temp, Integer> ent : tmp_set) {
				int reg = ent.getValue();
				tmp_content.remove(ent.getKey());
				reg_content[reg].remove(ent.getKey());
				if (nextUse.contains(ent.getKey()))
					code.add("sw " + reg_name[reg] + ", " + position.get(ent.getKey()) + "($sp)");
			}
			
			/*for (int i = 0; i < used_reg_count; ++ i)
				if (cost(i) > 0)
					code.add("sw " + reg_name[i] + ", " + (reg_pos + 4 + i * 4) + "($sp)");*/
			
			code.add("jal " + atom.function);
			code.add("lw $ra, " + reg_pos + "($sp)");
			
			/*for (int i = 0; i < used_reg_count; ++ i)
				if (cost(i) > 0)
					code.add("lw " + reg_name[i] + ", " + (reg_pos + 4 + i * 4) + "($sp)");*/
			
			//code.add("sw $v0, " + dst_pos + "($sp)");
			if (nextUse.contains(atom.result))
				code.add("move " + reg_name[pickReg2(atom.result)] + ", $v0");
		}
	}

	private void asm(BRANCH atom) {
		code.add(atom.type + " " + reg_name[pickReg(atom.src)] + ", " + atom.label);
	}

	private void asm(ARI atom) {
		if (!nextUse.contains(atom.dst)) return;
		
		int dst, src1, src2;
		if (!atom.src1.equal(atom.dst) && tmp_content.containsKey(atom.dst)) {
			if (!atom.src1.equal(atom.src2) && tmp_content.containsKey(atom.src2))
				src1 = pickReg(atom.src1, tmp_content.get(atom.dst), tmp_content.get(atom.src2));
			else
				src1 = pickReg(atom.src1, tmp_content.get(atom.dst));
		} else {
			if (!atom.src1.equal(atom.src2) && tmp_content.containsKey(atom.src2))
				src1 = pickReg(atom.src1, tmp_content.get(atom.src2));
			else
				src1 = pickReg(atom.src1);
		}
		if (!atom.src2.equal(atom.dst) && tmp_content.containsKey(atom.dst))
			src2 = pickReg(atom.src2, tmp_content.get(atom.dst), src1);
		else
			src2 = pickReg(atom.src2, src1);
		
		if (!atom.dst.equal(atom.src1) && nextUse.contains(atom.src1)) {
			if (!atom.dst.equal(atom.src2) && nextUse.contains(atom.src2)) {
				dst = pickReg2(atom.dst, src1, src2);
				if (dst == src1 || dst == src2) {
					reg_content[dst].remove(atom.dst);
					tmp_content.remove(atom.dst);
					dst = pickReg2(atom.dst, src1, src2);
				}
			} else {
				dst = pickReg2(atom.dst, src1);
				if (dst == src1) {
					reg_content[dst].remove(atom.dst);
					tmp_content.remove(atom.dst);
					dst = pickReg2(atom.dst, src1);
				}
			}
		} else {
			if (!atom.dst.equal(atom.src2) && nextUse.contains(atom.src2)) {
				dst = pickReg2(atom.dst, src2);
				if (dst == src2) {
					reg_content[dst].remove(atom.dst);
					tmp_content.remove(atom.dst);
					dst = pickReg2(atom.dst, src2);
				}
			} else dst = pickReg2(atom.dst);
		}
		code.add(atom.type + " " + reg_name[dst] + ", " + reg_name[src1] + ", " + reg_name[src2]);
	}
	
	
	private int cost(int i) {
		int ret = 0;
		for (Temp t : reg_content[i])
			ret += nextUse.contains(t) ? 1 : 0;
		return ret;
	}

	private void full_register_allocation(Translator func) {
		int index = 0;
		for (Map.Entry<Temp, Integer> ent : position.entrySet()) {
			tmp_content.put(ent.getKey(), index);
			//int pos = ent.getValue();
			if (params.contains(ent.getKey()))
				code.add("lw " + reg_name[index] + ", " + ent.getValue() + "($sp)");
			index ++;
		}
		for (Atom atom : func.atoms) {
			//System.out.println(atom.toString());
			if (atom instanceof ARI) {
				String l = reg_name[tmp_content.get(((ARI)atom).src1)];
				String r = reg_name[tmp_content.get(((ARI)atom).src2)];
				String d = reg_name[tmp_content.get(((ARI)atom).dst)];
				code.add(((ARI)atom).type + " " + d + ", " + l + ", " + r);
			} else if (atom instanceof BRANCH) {
				String l = reg_name[tmp_content.get(((BRANCH)atom).src)];
				code.add(((BRANCH)atom).type + " " + l + ", " + ((BRANCH)atom).label);
			} else if (atom instanceof CALL) {
				//TODO
				CALL cal = (CALL) atom;
				String dst = reg_name[tmp_content.get(cal.result)];
				
				if (cal.function.toString() == "_malloc") {
					//code.add("lw $a0, " + position.get(cal.params[0]) + "($sp)");
					String src = reg_name[tmp_content.get(cal.params[0])];
					code.add("move $a0, " + src);
					code.add("li $v0, 9");
					code.add("syscall");
					code.add("move " + dst + ", $v0");
				} else if (cal.function.toString() == "printInt"){
					String src = reg_name[tmp_content.get(cal.params[0])];
					code.add("move $a0, " + src);
					code.add("li $v0, 1");
					code.add("syscall");
					
				} else if (cal.function.toString() == "printChar"){
					String src = reg_name[tmp_content.get(cal.params[0])];
					code.add("move $a0, " + src);
					code.add("sb $a0, 0($sp)");
					code.add("sb $zero, 1($sp)");
					code.add("move $a0, $sp");
					code.add("li $v0, 4");
					code.add("syscall");
					
				} else if (cal.function.toString() == "printString"){
					String src = reg_name[tmp_content.get(cal.params[0])];
					code.add("move $a0, " + src);
					code.add("li $v0, 4");
					code.add("syscall");
					
				} else if (cal.function.toString() == "readInt"){
					code.add("li $v0, 5");
					code.add("syscall");
					code.add("move " + dst + ", $v0");
				} else {
					code.add("sw $ra, " + reg_pos + "($sp)");
					for (Map.Entry<Temp, Integer> ent : tmp_content.entrySet()) {
						Temp t = ent.getKey();
						String reg = reg_name[ent.getValue()];
						int pos = position.get(t);
						code.add("sw " + reg + ", " + pos + "($sp)");
					}
					int i = 0;
					for (Temp t : cal.params) {
						String reg = reg_name[tmp_content.get(t)];
						code.add("sw " + reg + ", " + i * 4 + "($sp)");
						i ++;
					}
					code.add("jal " + cal.function);
					code.add("lw $ra, " + reg_pos + "($sp)");for (Map.Entry<Temp, Integer> ent : tmp_content.entrySet()) {
						Temp t = ent.getKey();
						String reg = reg_name[ent.getValue()];
						int pos = position.get(t);
						code.add("lw " + reg + ", " + pos + "($sp)");
					}
					code.add("move " + dst + ", $v0");
				}
			} else if (atom instanceof COMP) {
				String l = reg_name[tmp_content.get(((COMP)atom).src1)];
				String r = reg_name[tmp_content.get(((COMP)atom).src2)];
				code.add(((COMP)atom).type + " " + l + ", " + r + ", " + ((COMP)atom).label);
			} else if (atom instanceof IARI) {
				String l = reg_name[tmp_content.get(((IARI)atom).src1)];
				int r = ((IARI)atom).src2;
				String d = reg_name[tmp_content.get(((IARI)atom).dst)];
				code.add(((IARI)atom).type + " " + d + ", " + l + ", " + r);
			} else if (atom instanceof JMP) {
				code.add(((JMP)atom).type + " " + ((JMP)atom).label);
			} else if (atom instanceof LA) {
				Label l = ((LA)atom).src;
				String d = reg_name[tmp_content.get(((LA)atom).dst)];
				code.add("la " + d + ", " + l);
			} else if (atom instanceof LabelAtom) {
				code.add(atom.toString());
			} else if (atom instanceof LI) {
				int l = ((LI)atom).imm;
				String d = reg_name[tmp_content.get(((LI)atom).dst)];
				code.add("li " + d + ", " + l);
			} else if (atom instanceof MEM) {
				String l = reg_name[tmp_content.get(((MEM)atom).src)];
				String d = reg_name[tmp_content.get(((MEM)atom).dst)];
				code.add(((MEM)atom).type + " " + d + ", " + ((MEM)atom).offset + "(" + l + ")");
			} else if (atom instanceof RETURN) {
				RETURN ret = (RETURN)atom;
				if (ret.value instanceof Const) {
					code.add("li $v0, " + ((Const)ret.value).value);
				} else {
					Temp t = ((TempOprand)ret.value).temp;
					String s = reg_name[tmp_content.get(t)];
					code.add("move $v0, " + s);
				}
				
				code.add("addi $sp, $sp," + frame_size);
				code.add("jr $ra");
			} else if (atom instanceof SARI) {
				String l = reg_name[tmp_content.get(((SARI)atom).src)];
				String d = reg_name[tmp_content.get(((SARI)atom).dst)];
				code.add(((SARI)atom).type + " " + d + ", " + l);
			}
		}
	}
	
	@Override
	public String toString() {
		String ret = "";
		for (String s : code)
			ret = ret + s + "\n";
		return ret;
	}

	private int pickReg2(Temp t) {
		if (tmp_content.containsKey(t)) {
			int ret = tmp_content.get(t);
			boolean needrefactor = false;
			for (Temp tmp : reg_content[ret])
				if (!t.equal(tmp) && nextUse.contains(tmp)) {
					needrefactor = true;
					break;
				}
			if (!needrefactor) return ret;
			else {
				reg_content[ret].remove(t);
				tmp_content.remove(t);
			}
		}
		int min = 2147483647, ret = -1;
		for (int i = 0; i < used_reg_count; ++ i) {
			int tmp = cost(i);
			if (tmp < min) {
				min = tmp;
				ret = i;
			}
		}
		for (Temp tmp : reg_content[ret]) {
			if (nextUse.contains(tmp))
				code.add("sw " + reg_name[ret] + ", " + position.get(tmp) + "($sp)");
			tmp_content.remove(tmp);
		}
		//code.add("lw " + reg_name[ret] + ", " + position.get(t) + "($sp)");
		tmp_content.put(t, ret);
		reg_content[ret].clear();
		reg_content[ret].add(t);
		return ret;
	}
	
	private int pickReg2(Temp t, int avoid) {
		if (tmp_content.containsKey(t)) {
			int ret = tmp_content.get(t);
			boolean needrefactor = false;
			for (Temp tmp : reg_content[ret])
				if (!t.equal(tmp) && nextUse.contains(tmp)) {
					needrefactor = true;
					break;
				}
			if (!needrefactor) return ret;
			else {
				reg_content[ret].remove(t);
				tmp_content.remove(t);
			}
		}
		int min = 2147483647, ret = -1;
		for (int i = 0; i < used_reg_count; ++ i) {
			if (i == avoid) continue;
			int tmp = cost(i);
			if (tmp < min) {
				min = tmp;
				ret = i;
			}
		}
		for (Temp tmp : reg_content[ret]) {
			if (nextUse.contains(tmp))
				code.add("sw " + reg_name[ret] + ", " + position.get(tmp) + "($sp)");
			tmp_content.remove(tmp);
		}
		//code.add("lw " + reg_name[ret] + ", " + position.get(t) + "($sp)");
		tmp_content.put(t, ret);
		reg_content[ret].clear();
		reg_content[ret].add(t);
		return ret;
	}
	
	private int pickReg2(Temp t, int avoid1, int avoid2) {
		if (tmp_content.containsKey(t)) {
			int ret = tmp_content.get(t);
			boolean needrefactor = false;
			for (Temp tmp : reg_content[ret])
				if (!t.equal(tmp) && nextUse.contains(tmp)) {
					needrefactor = true;
					break;
				}
			if (!needrefactor) return ret;
			else {
				reg_content[ret].remove(t);
				tmp_content.remove(t);
			}
		}
		int min = 2147483647, ret = -1;
		for (int i = 0; i < used_reg_count; ++ i) {
			if (i == avoid1 || i == avoid2) continue;
			int tmp = cost(i);
			if (tmp < min) {
				min = tmp;
				ret = i;
			}
		}
		for (Temp tmp : reg_content[ret]) {
			if (nextUse.contains(tmp))
				code.add("sw " + reg_name[ret] + ", " + position.get(tmp) + "($sp)");
			tmp_content.remove(tmp);
		}
		//code.add("lw " + reg_name[ret] + ", " + position.get(t) + "($sp)");
		tmp_content.put(t, ret);
		reg_content[ret].clear();
		reg_content[ret].add(t);
		return ret;
	}
	
	private int pickReg(Temp t) {
		if (tmp_content.containsKey(t)) return tmp_content.get(t);
		int min = 2147483647, ret = -1;
		for (int i = 0; i < used_reg_count; ++ i) {
			int tmp = cost(i);
			if (tmp < min) {
				min = tmp;
				ret = i;
			}
		}
		for (Temp tmp : reg_content[ret]) {
			if (nextUse.contains(tmp))
				code.add("sw " + reg_name[ret] + ", " + position.get(tmp) + "($sp)");
			tmp_content.remove(tmp);
		}
		code.add("lw " + reg_name[ret] + ", " + position.get(t) + "($sp)");
		tmp_content.put(t, ret);
		reg_content[ret].clear();
		reg_content[ret].add(t);
		return ret;
	}
	
	private int pickReg(Temp t, int avoid) {
		if (tmp_content.containsKey(t)) return tmp_content.get(t);
		int min = 2147483647, ret = -1;
		for (int i = 0; i < used_reg_count; ++ i) {
			if (i == avoid) continue;
			int tmp = cost(i);
			if (tmp < min) {
				min = tmp;
				ret = i;
			}
		}
		for (Temp tmp : reg_content[ret]) {
			if (nextUse.contains(tmp))
				code.add("sw " + reg_name[ret] + ", " + position.get(tmp) + "($sp)");
			tmp_content.remove(tmp);
		}
		code.add("lw " + reg_name[ret] + ", " + position.get(t) + "($sp)");
		tmp_content.put(t, ret);
		reg_content[ret].clear();
		reg_content[ret].add(t);
		return ret;
	}
	
	private int pickReg(Temp t, int avoid1, int avoid2) {
		if (tmp_content.containsKey(t)) return tmp_content.get(t);
		int min = 2147483647, ret = -1;
		for (int i = 0; i < used_reg_count; ++ i) {
			if (i == avoid1 || i == avoid2) continue;
			int tmp = cost(i);
			if (tmp < min) {
				min = tmp;
				ret = i;
			}
		}
		for (Temp tmp : reg_content[ret]) {
			if (nextUse.contains(tmp))
				code.add("sw " + reg_name[ret] + ", " + position.get(tmp) + "($sp)");
			tmp_content.remove(tmp);
		}
		code.add("lw " + reg_name[ret] + ", " + position.get(t) + "($sp)");
		tmp_content.put(t, ret);
		reg_content[ret].clear();
		reg_content[ret].add(t);
		return ret;
	}
}

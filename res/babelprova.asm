	.text
	.align	2
	.globl	main
	.data
error:
	.asciiz "Index fora de Rang"
	.text
main:
	move $fp, $sp
	li   $t0, 1
	sw   $t0, -0($gp)
	li   $t0, 1
	sw   $t0, -4($gp)
	li   $t0, 1
	sw   $t0, -8($gp)
	li   $t0, 1
	sw   $t0, -28($gp)
	li   $t0, -1
	sw   $t0, -4($gp)
	li   $t0, 3
	sw   $t0, -0($gp)
	lb   $t0, -0($gp)
	li   $t1, 3
	blt   $t0, $t1, E1
	li   $t1, 7
	bgt   $t0, $t1, E1
	li   $t1, 3
	li   $t2, 4
	mul   $t1, $t1, $t2
	li   $t2, 8
	subu   $t1, $t2, $t1
	li   $t2, 4
	mul   $t2, $t0, $t2
	addu   $t1, $t2, $t1
	la   $t2, 0($gp)
	subu   $t1, $t2, $t1
	li   $t2, -1
	sw   $t2, 0($t1)
	b	E2
E1:
	li   $v0, 4
	la   $a0, error
	syscall
	li   $v0, 10
	syscall
E2:
	jr $ra
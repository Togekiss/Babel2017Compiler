	.text
	.align	2
	.globl	main
	.data
error:
	.asciiz "[ERR_GC_1] Index de vector fora de limits"
	.text
main:
	move $fp, $sp
	.data
E1: .asciiz "ho%&/ajn l·l egfdsa"
	.text
	li   $t0, 1
	sw   $t0, -0($gp)
	li   $t0, 1
	sw   $t0, -4($gp)
	li $t0, 3
	li $t0, 7
	li   $t0, 1
	sw   $t0, -8($gp)
	li   $t0, 1
	sw   $t0, -28($gp)
	li $t0, 1
	li   $t1, 1
	sw   $t1, -4($gp)
	li $t1, 3
	li   $t2, 3
	sw   $t2, -0($gp)
	lw   $t2, -0($gp)
	lw   $t3, -0($gp)
	li   $t4, 3
	blt   $t3, $t4, E2
	li   $t4, 7
	bgt   $t3, $t4, E2
	li $t4, 1
	li   $t5, 3
	li   $t6, 4
	mul   $t5, $t5, $t6
	li   $t6, 8
	subu   $t5, $t6, $t5
	li   $t6, 4
	mul   $t6, $t3, $t6
	addu   $t5, $t6, $t5
	la   $t6, 0($gp)
	subu   $t5, $t6, $t5
	li   $t6, 1
	sw   $t6, 0($t5)
	b	E3
E2:
	li   $v0, 4
	la   $a0, error
	syscall
	li   $v0, 10
	syscall
E3:
	jr $ra
